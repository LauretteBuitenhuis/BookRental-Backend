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
	
	// TODO change from Copy to status. Does not need to return the created copy.
	@PostMapping("copy/create/{bookId}")
	public Copy createCopy(@PathVariable long bookId) {
		Book book = bookRepo.findById(bookId).get();
		Copy copy = new Copy(true, book);
		
		book.addCopy(copy);
		
		copyRepo.save(copy);
		bookRepo.save(book);
		
		return copy;
	}
}
