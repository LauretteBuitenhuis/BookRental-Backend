package nl.workingtalent.bookrental.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
		

		Tag tagName = new Tag(tag);
		
		// If Tag already exists overwrite tagName with existing tag
		if (tagRepo.findByName(tag)!=null) {
			tagName = tagRepo.findByName(tag);
		}
		
		tagName.addBook(book);
		book.addTag(tagName);
		
		tagRepo.save(tagName);
		bookRepo.save(book);
		return tagName;
	}
	
	@DeleteMapping("tag/{id}/delete")
	public Map<String, String> delete(@RequestHeader(name = "Authorization") String token, @PathVariable long id) {

		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for deleting book");
		}

		tagRepo.deleteById(id);

		Map<String, String> map = new HashMap<String, String>();
		map.put("status", "succes");
		return map;
	}

}
