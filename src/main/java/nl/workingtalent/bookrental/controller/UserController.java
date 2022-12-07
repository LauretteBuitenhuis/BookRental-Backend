package nl.workingtalent.bookrental.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import nl.workingtalent.bookrental.model.GeneratePassword;
import nl.workingtalent.bookrental.model.NewUser;
import nl.workingtalent.bookrental.model.PasswordEncoder;
import nl.workingtalent.bookrental.model.User;
import nl.workingtalent.bookrental.repository.IUserRepository;

@RestController
@CrossOrigin(maxAge = 3600)
public class UserController {
	
	@Autowired
	private IUserRepository repo;
	
	@PostMapping("user/create")
	public void createUser(@RequestBody NewUser userRequest) {
		// TODO - check if user is admin (Authorised to create new user)
		
		// Check if user already exists in database by email
		boolean userAlreadyExists = false;
		User existingUser = repo.findByEmail(userRequest.getEmail());
		if (existingUser != null) {
			userAlreadyExists = true;
		}
		// if not in database: create account
		if(userAlreadyExists == false) {
			String generatedPassword = new GeneratePassword().generateRandomString(10);
			String encodedPassword = new PasswordEncoder().encode(generatedPassword);
			User user = new User();
			
			user.setFirstName(userRequest.getFirstName());
			user.setLastName(userRequest.getLastName());
			user.setEmail(userRequest.getEmail());
			user.setUsername(userRequest.getEmail());
			user.setPassword(encodedPassword);
			
			// TODO - false by default, what if it is a admin?
			user.setAdmin(false);
			repo.save(user);
			
		// If already in database: error message (403/409?)
		} else {
			System.out.println("Email already exists.");
		}
	}
	
	@GetMapping("user/all")
	public List<User> findAllUsers(){
		return repo.findAll();
	}
}
