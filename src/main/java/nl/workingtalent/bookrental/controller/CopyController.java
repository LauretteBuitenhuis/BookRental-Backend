package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.model.Copy;
import nl.workingtalent.bookrental.repository.IBookRepository;
import nl.workingtalent.bookrental.repository.ICopyRepository;

@RestController
@CrossOrigin(maxAge = 3600)
public class CopyController {
	
	@Autowired
	private ICopyRepository copyRepo;
	@Autowired
	private IBookRepository bookRepo;
	@Autowired
	private UserController userController;
	
	@PostMapping("copy/create/{bookId}")
	public Copy createCopy(@RequestHeader(name = "Authorization") String token, @PathVariable long bookId) {
		
		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for creating Copy");
		}
		
		Book book = bookRepo.findById(bookId).get();
		Copy copy = new Copy(book);
		
		book.addCopy(copy);
		
		copyRepo.save(copy);
		bookRepo.save(book);
		
		return copy;
	}
}
