package nl.workingtalent.bookrental.controller;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
	public String createUser(@RequestHeader(name = "Authorization") String token, @RequestBody NewUserDto newUserDto) {
		
		User loggedInUser = repo.findByToken(token);

		// Check if user had Admin rights
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			return "No permission";
		}
		
		// Check if user already exists in database by email
		User existingUser = repo.findByEmail(newUserDto.getEmail());
		if (existingUser != null) {
			return "Email already exists";
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
		
		return null;
 	}
	
	
	@PostMapping("user/login")
	public String userLogin(@RequestBody LoginDto loginDto) {		
		User foundUser = repo.findByEmail(loginDto.getEmail());
		
		// Check if mail address exist in database
		if (foundUser == null) {
			System.out.println("User does not exist");
			return null;
		}
		
		if (passwordEncoder.matches(loginDto.getPassword(), foundUser.getPassword())) {
			foundUser.setToken(RandomStringUtils.random(150, true, true));
			repo.save(foundUser);
			
			System.out.println("Successfull login");
			
			return foundUser.getToken();
		}

		System.out.println("Invalid password, try again");
		
		return null;
	}
	
	
	@GetMapping("user/all")
	public List<User> findAllUsers(){
		return repo.findAll();
	}
}
