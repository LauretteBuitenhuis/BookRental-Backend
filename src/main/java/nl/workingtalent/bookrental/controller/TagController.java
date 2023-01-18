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
import nl.workingtalent.bookrental.model.Tag;
import nl.workingtalent.bookrental.repository.IBookRepository;
import nl.workingtalent.bookrental.repository.ITagRepository;

@RestController
@CrossOrigin(maxAge = 3600)
public class TagController {
	
	@Autowired
	private ITagRepository tagRepo;
	@Autowired
	private IBookRepository bookRepo;
	
	@Autowired
	private UserController userController;
	
	@PostMapping("tag/create/{bookId}")
	public Tag createTag(@RequestHeader(name="Authorization") String token, @RequestBody String tag, @PathVariable long bookId) {
		
		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for creating Copy");
		}
		
		Book book = bookRepo.findById(bookId).get();
		
//		 Check if Tag already exists
//		 TODO Anybody knows how I can remove the repeated code in if/else statement?
		
		Tag tagName = new Tag(tag);
		tagName.addBook(book);
		book.addTag(tagName);
		
		tagRepo.save(tagName);
		bookRepo.save(book);
		return tagName;
	}
		
//		if(tagRepo.findByName(tag)==null) {
//			System.out.println("Tag does not exist yet");
//			Tag tagName = new Tag(tag,book);
//			tagRepo.save(tagName);
//			book.addTag(tagName);
//			bookRepo.save(book);
//			//
//			return tagName;
//		}
//		else {
//			System.out.println("tag does already exist");
//			Tag tagName = tagRepo.findByName(tag);
//			book.addTag(tagName);
//			bookRepo.save(book);
//			
//			return tagName;
//		}
//		

}
