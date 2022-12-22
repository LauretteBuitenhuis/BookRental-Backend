package nl.workingtalent.bookrental.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import nl.workingtalent.bookrental.dto.LoginDto;
import nl.workingtalent.bookrental.dto.NewUserDto;
import nl.workingtalent.bookrental.model.Book;
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
		User createdUser = repo.save(user);
		
		// Send email verification
		emailService.sendEmail(newUserDto);
		
		return user.getId(); 
 	}
	
	@GetMapping("dummydata/users")
	public List<User> insertDummyUsers() {
		ObjectMapper mapper = new ObjectMapper();
		
		InputStream inputStream = Book.class.getResourceAsStream("/dummy-users.json");
		CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, NewUserDto.class);

		List<NewUserDto> users = new ArrayList<NewUserDto>();
		
		try {
			users = mapper.readValue(inputStream, collectionType);
		} catch (IOException e) {
			System.out.println("Error loading JSON file");
		}
		
		for (NewUserDto user : users) {
			
			// Admin account needs to exist for proper rights
			createUser("admin", user);
		}
		
		return findAllUsers();
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
	
	// TODO: Change return type to status
	public boolean userIsLoggedIn(String token)
	{
		User loggedInUser = repo.findByToken(token);
		
		if (loggedInUser == null) {
			// TODO: Redirect user to login page
			return false; // User not logged in
		}
		
		// User is logged in, add success code
		return true;
	}
	
	// TODO: Change return type to status
	public boolean userIsAdmin(String token)
	{
		// Override permissions if it is an automated process 
		if (token.equalsIgnoreCase("admin")) return true;
		
		// Check if user is logged in
		if (!userIsLoggedIn(token)) { 
			return false;
		}
		
		User loggedInUser = repo.findByToken(token);
		
		if (!loggedInUser.isAdmin()) {
			// User has invalid permissions
			return false;
		}
		
		// User is logged in, and has admin permissions
		return true;
	}
	
	// TODO: Change return type to status
	public boolean userIdMatches(String token, long userId) {
		
		// Check if user is logged in
		if (!userIsLoggedIn(token)) { 
			return false;
		}
		
		// Override permission check if an admin does it
		if (userIsAdmin(token)) {
			return true;
		}
		
		User loggedInUser = repo.findByToken(token);
		
		// Check if intended user id is the same as the actual user id
		// This is done to prevent changing 
		return userId == loggedInUser.getId();
	}
	
	
	@GetMapping("user/all")
	public List<User> findAllUsers(){
		return repo.findAll();
	}
}
