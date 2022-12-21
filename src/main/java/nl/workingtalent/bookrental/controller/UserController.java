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
	public ResponseStatusException createUser(@RequestHeader(name = "Authorization") String token, @RequestBody NewUserDto newUserDto) {
		
		User loggedInUser = repo.findByToken(token);

		// Check if user has Admin rights
		if (loggedInUser == null || !loggedInUser.isAdmin()) 
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No permission"); // 401
		
		// Check if user already exists in database by email
		User existingUser = repo.findByEmail(newUserDto.getEmail());
		if (existingUser != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists"); // 409
		}
		
		String encodedPassword = passwordEncoder.encode(newUserDto.getPassword());
		User user = new User();	
		user.setFirstName(newUserDto.getFirstName());
		user.setLastName(newUserDto.getLastName());
		user.setEmail(newUserDto.getEmail());
		user.setPassword(encodedPassword);
		user.setEnabled(false);
		user.setAdmin(newUserDto.isAdmin());
		
		repo.save(user);
		
		// Send email verification
		emailService.sendEmail(newUserDto);
		
		return new ResponseStatusException(HttpStatus.CREATED, "User created"); // 201
 	}
	
	// TODO fix: Direct self-reference leading to cycle 
	@PostMapping("user/login")
	public ResponseStatusException userLogin(@RequestBody LoginDto loginDto) {		
		User foundUser = repo.findByEmail(loginDto.getEmail());
		
		// Check if mail address exists in database
		if (foundUser == null) {
			return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"); // 404
		}
		
		if (passwordEncoder.matches(loginDto.getPassword(), foundUser.getPassword())) {
			foundUser.setToken(RandomStringUtils.random(150, true, true));
			repo.save(foundUser);
			
			System.out.println(foundUser.getToken());
			
			return new ResponseStatusException(HttpStatus.OK, "Login succesful"); // 200
		}
		
		return new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid, please try again"); // 403
	}
	
	
	@GetMapping("user/all")
	public List<User> findAllUsers(){
		return repo.findAll();
	}
}
