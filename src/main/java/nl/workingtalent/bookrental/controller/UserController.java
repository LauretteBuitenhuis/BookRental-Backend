package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import nl.workingtalent.bookrental.model.User;
import nl.workingtalent.bookrental.repository.IUserRepository;

@RestController
@CrossOrigin(maxAge = 3600)
public class UserController {
	
	@Autowired
	private IUserRepository repo;
	
	@PostMapping("user/create")
	public void createUser(@RequestBody User user) {
		repo.save(user);
	}
	
	
	@GetMapping("user/login")
	public boolean userLogin(){
		System.out.println("Werkt!");
		User userInlogMail=repo.findByEmail("12345@gmail.com");
		System.out.println("Id belonging to email is: " + userInlogMail.getId());
		User userInlogPassword=repo.findByPassword("welkom12345");
		System.out.println("Id belonging to password is: "+ userInlogPassword.getId());
		return userInlogPassword.getId() == userInlogMail.getId();
		
	}
	
}
