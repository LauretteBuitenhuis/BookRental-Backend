package nl.workingtalent.bookrental.controller;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.model.User;
import nl.workingtalent.bookrental.repository.IBookRepository;
import nl.workingtalent.bookrental.repository.IUserRepository;

@RestController
@CrossOrigin(maxAge = 3600)
public class BookController {
	
	@Autowired
	private IBookRepository repo;
	
	@Autowired
	private IUserRepository userRepo;
	
	@Autowired
	private UserController userController;
	
	@Autowired
	private CopyController copyController;
	
	@PostMapping("book/create")
	public Book createBook(@RequestHeader(name = "Authorization") String token, @RequestBody Book book) {
		
		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for creating book");
		}
		
		repo.save(book);
		return book;
	}
	
	@DeleteMapping("book/{id}/delete")
	public void delete(@RequestHeader(name = "Authorization") String token, @PathVariable long id) {
		
		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for deleting book");
		}
		
		repo.deleteById(id);
	}
	
	@GetMapping("dummydata/books")
	public List<Book> insertDummyBooks() {
		ObjectMapper mapper = new ObjectMapper();
		
		InputStream inputStream = Book.class.getResourceAsStream("/dummy-books.json");
		CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Book.class);

		List<Book> books = new ArrayList<Book>();
		
		try {
			books = mapper.readValue(inputStream, collectionType);
		} catch (IOException e) {
			System.out.println("Error loading JSON file");
		}
		
		for (Book book : books) {
			
			Book databaseBook = createBook("admin", book);
			
			// Generate random amount of copies between 1 and 3
			Random random = new Random();
			for (int i =0; i < random.ints(1, 4).findFirst().getAsInt(); i++) {
				copyController.createCopy("admin", databaseBook.getId());
			}
		}
		
		return findAllBooks();
    }
	
	@PutMapping("book/{id}/edit")
	public void editBook(@RequestHeader(name = "Authorization") String token, @RequestBody Book book, @PathVariable long id) {
		
		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for editing book");
		}
		
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
