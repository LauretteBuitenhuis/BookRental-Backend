package nl.workingtalent.bookrental.controller;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.repository.IBookRepository;

@RestController
@CrossOrigin(maxAge = 3600)
public class BookController {
	
	@Autowired
	private IBookRepository repo;
	
	@Autowired
	private CopyController copyController;
	
	@PostMapping("book/create")
	public Book createBook(@RequestBody Book book) {
		repo.save(book);
		return book;
	}
	
	@DeleteMapping("book/{id}/delete")
	public void delete(@PathVariable long id) {
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
			return books;
		} catch (IOException e) {
			System.out.println("Error loading JSON file");
		}
		
		for (Book book : books) {
			Book databaseBook = createBook(book);
			copyController.createCopy(databaseBook.getId());
		}
		
		return findAllBooks();
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
