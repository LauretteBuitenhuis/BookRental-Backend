package nl.workingtalent.bookrental.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
		// TODO - WIM-12: check if user is admin (Authorised to create new user)
		
		// Check if user already exists in database by email
		User existingUser = repo.findByEmail(userRequest.getEmail());
		if (existingUser != null) {
			System.out.println("Email already exists.");
			return;
		}
		
		// if not in database: create account
		String generatedPassword = new GeneratePassword().generateRandomString(10);
		
		// show random generated password for development
		// TODO remove this line
		System.out.println("The default password is " + generatedPassword);
		
		String encodedPassword = new PasswordEncoder().encode(generatedPassword);
		System.out.println("The default ecoded password is " + encodedPassword);
		User user = new User();
			
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setEmail(userRequest.getEmail());
		user.setPassword(encodedPassword);
		user.setEnabled(false);
		// User is logged out by default
		user.setLogIn(false);
			
		// TODO - WIM-12: admin false by default, what if it is a admin?
		user.setAdmin(false);
		repo.save(user);			
}
	
	
	@GetMapping("user/login")
	public void userLogin(@RequestBody User user){		
		User userInlog = repo.findByEmail(user.getEmail());
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		// Check if mail address exist in database
		if (userInlog==null){
			System.out.println("User does not exist");
		}
		
		else if (passwordEncoder.matches(user.getPassword(),userInlog.getPassword())) {
			// set logged in boolean for this user
			userInlog.setLogIn(true);
			repo.save(userInlog);
			
			// TODO redirect to the page that was originally opened
			
			System.out.println("Successfull login");	
		}

		else {
			System.out.println("Invalid password, try again");
		}
	}
	
	
	@GetMapping("user/all")
	public List<User> findAllUsers(){
		return repo.findAll();
	}
}
