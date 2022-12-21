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

@RestController
@CrossOrigin(maxAge = 3600)
public class UserController {
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Autowired
	private IUserRepository repo;
	
	@PostMapping("user/create")
	public String createUser(@RequestHeader(name = "Authorization") String token, @RequestBody NewUserDto newUserDto) {
		User loggedInUser = repo.findByToken(token);
		//TODO add error codes 
		if (loggedInUser == null || !loggedInUser.isAdmin()) {
			return "No permission";
		}
		
		// Check if user already exists in database by email
		User existingUser = repo.findByEmail(newUserDto.getEmail());
		if (existingUser != null) {
			System.out.println("Email already exists.");
			return "Email exists";
		}

		String encodedPassword = passwordEncoder.encode(newUserDto.getPassword());
		// TODO constructor		
		User user = new User(newUserDto.getFirstName(), 
				newUserDto.getLastName(), 
				newUserDto.getEmail(), 
				encodedPassword, 
				newUserDto.isAdmin(),false);
		repo.save(user);
		
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
