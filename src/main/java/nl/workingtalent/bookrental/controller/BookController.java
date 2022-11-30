package nl.workingtalent.bookrental.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.repository.IBookRepository;

@RestController
@CrossOrigin(maxAge = 3600)

public class BookController {
	
	@Autowired
	private IBookRepository repo;
	
	
	@RequestMapping(method = RequestMethod.POST, value="createbook")
	public Book makeBook(@RequestBody Book book) {
		Book newBook=repo.save(book);
		
		return newBook;
	}
	
	@GetMapping("book/all")
	public List<Book> findAllBooks(){
		return repo.findAll();
	}

}
