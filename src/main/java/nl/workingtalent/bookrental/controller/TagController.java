package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
	
	@PostMapping("tag/create/{bookId}")
	public void createTag(@RequestBody Tag tag, @PathVariable long bookId) {
		Book book = bookRepo.findById(bookId).get();
		tag.setBook(book);
		tagRepo.save(tag);
	}
}
