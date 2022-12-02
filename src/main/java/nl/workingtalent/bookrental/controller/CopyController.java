package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
	
	@PostMapping("copy/create/{bookId}")
	public void createCopy(@RequestBody Copy copy, @PathVariable long bookId) {
		Book book = bookRepo.findById(bookId).get();
		copy.setBook(book);
		copyRepo.save(copy);
	}
}
