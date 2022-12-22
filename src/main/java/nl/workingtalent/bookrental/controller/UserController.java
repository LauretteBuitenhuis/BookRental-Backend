package nl.workingtalent.bookrental.controller;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import nl.workingtalent.bookrental.dto.LoginDto;
import nl.workingtalent.bookrental.dto.NewUserDto;
import nl.workingtalent.bookrental.model.User;
import nl.workingtalent.bookrental.repository.IUserRepository;
import nl.workingtalent.bookrental.services.EmailService;

@RestController
@CrossOrigin(maxAge = 3600)
public class UserController {
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Autowired
	private IUserRepository repo;
	
	@Autowired
	private EmailService emailService;
	
	@PostMapping("user/create")
	@ResponseStatus(code=HttpStatus.CREATED)
	public long createUser(@RequestHeader(name = "Authorization") String token, @RequestBody NewUserDto newUserDto) {
		
		User loggedInUser = repo.findByToken(token);
    
		// Check if user has Admin rights
		if (loggedInUser == null || !loggedInUser.isAdmin()) 
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No permission");

		
		// Check if user already exists in database by email
		User existingUser = repo.findByEmail(newUserDto.getEmail());
		if (existingUser != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
		}
		
		String encodedPassword = passwordEncoder.encode(newUserDto.getPassword());
		
		User user = new User(newUserDto.getFirstName(), 
				newUserDto.getLastName(), 
				newUserDto.getEmail(), 
				encodedPassword, 
				newUserDto.isAdmin(),false);
		repo.save(user);
		
		// Send email verification
		emailService.sendEmail(newUserDto);
		
		return createdUser.getId(); 
 	}
	
	
	@PostMapping("user/login")
	public String userLogin(@RequestBody LoginDto loginDto) {		
		User foundUser = repo.findByEmail(loginDto.getEmail());
		
		if (foundUser == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		
		boolean hasMatchingPassword = passwordEncoder.matches(loginDto.getPassword(), foundUser.getPassword());
		
		if (!hasMatchingPassword) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong username or password, please try again");
		}
		
		String token = RandomStringUtils.random(150, true, true);
		foundUser.setToken(token);
		repo.save(foundUser);
					
		return token;
	}
	
	
	@GetMapping("user/all")
	public List<User> findAllUsers(){
		return repo.findAll();
	}
}
