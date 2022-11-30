package nl.workingtalent.bookrental.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	
	@DeleteMapping("book/{id}/delete")
	public void delete(@PathVariable long id) {
		repo.deleteById(id);
	}
	
	@PutMapping("book/{id}/edit")
	public void editBook(@RequestBody Book book, @PathVariable long id) {
		Book prevBook = repo.findById(id).get();
		
		prevBook.setAuthor(book.getAuthor());
		prevBook.setIsbn(book.getIsbn());
		prevBook.setTitle(book.getTitle());
		
		repo.save(prevBook);
		
	}
	
	@GetMapping("book/all")
	public List<Book> findAllBooks(){
		return repo.findAll();
	}
	
	@GetMapping("book/{id}")
	public Book findBook(@PathVariable long id){
		return repo.findById(id).get();
	}
	
	
}
