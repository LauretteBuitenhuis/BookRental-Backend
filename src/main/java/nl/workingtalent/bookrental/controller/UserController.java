package nl.workingtalent.bookrental.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import nl.workingtalent.bookrental.model.Book;
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
		String encodedPassword = new PasswordEncoder().encode(generatedPassword);
		User user = new User();

		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setEmail(userRequest.getEmail());
		user.setPassword(encodedPassword);
		user.setEnabled(false);

		// TODO - WIM-12: admin false by default, what if it is a admin?
		user.setAdmin(false);
		repo.save(user);
	}

	@GetMapping("user/all")
	public List<User> findAllUsers() {
		return repo.findAll();
	}
	
	@GetMapping("dummydata/users")
	public List<User> insertDummyBooks() {
		ObjectMapper mapper = new ObjectMapper();
		
		InputStream inputStream = Book.class.getResourceAsStream("/dummy-users.json");
		CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, NewUser.class);

		List<NewUser> users = new ArrayList<NewUser>();
		
		try {
			users = mapper.readValue(inputStream, collectionType);
		} catch (IOException e) {
			System.out.println("Error loading JSON file");
		}
		
		for (NewUser user : users) {
			
			createUser(user);
			
		}
		
		return findAllUsers();
    }
}
