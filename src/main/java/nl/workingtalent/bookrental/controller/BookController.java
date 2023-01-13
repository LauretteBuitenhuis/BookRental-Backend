package nl.workingtalent.bookrental.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import nl.workingtalent.bookrental.dto.NewBookDto;
import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.model.Copy;
import nl.workingtalent.bookrental.model.Loan;
import nl.workingtalent.bookrental.model.Reservation;
import nl.workingtalent.bookrental.repository.IBookRepository;
import nl.workingtalent.bookrental.repository.ICopyRepository;
import nl.workingtalent.bookrental.repository.IUserRepository;

@RestController
@CrossOrigin(maxAge = 3600)
public class BookController {

	@Autowired
	private IBookRepository bookRepo;

	@Autowired
	private ICopyRepository copyRepo;

	@Autowired
	private IUserRepository userRepo;

	@Autowired
	private UserController userController;

	@Autowired
	private CopyController copyController;

	@PostMapping("book/create")
	public Book createBook(@RequestHeader(name = "Authorization") String token, @RequestBody NewBookDto NewBookDto) {

		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for creating book");
		}

		Book book = new Book(NewBookDto.getTitle(),NewBookDto.getAuthor(),NewBookDto.getIsbn());
		bookRepo.save(book);
		return book;
	}

	@DeleteMapping("book/{id}/delete")
	public Map<String, String> delete(@RequestHeader(name = "Authorization") String token, @PathVariable long id) {

		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for deleting book");
		}

		bookRepo.deleteById(id);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("status", "succes");
		return map;
	}

	@GetMapping("dummydata/books")
	public List<Book> insertDummyBooks() {
		ObjectMapper mapper = new ObjectMapper();

		InputStream inputStream = NewBookDto.class.getResourceAsStream("/dummy-books.json");
		CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, NewBookDto.class);

		List<NewBookDto> books = new ArrayList<NewBookDto>();
		System.out.println(books);

		try {
			books = mapper.readValue(inputStream, collectionType);
		} catch (IOException e) {
			System.out.println("Error loading JSON file");
		}

		for (NewBookDto book : books) {

			Book databaseBook = createBook("admin", book);

			// Generate random amount of copies between 1 and 3
			Random random = new Random();
			for (int i = 0; i < random.ints(1, 4).findFirst().getAsInt(); i++) {
				copyController.createCopy("admin", databaseBook.getId());
			}
		}

		return findAllBooks();
	}

	@PutMapping("book/{id}/edit")
	public Map<String, String> editBook(@RequestHeader(name = "Authorization") String token, @RequestBody Book book,
			@PathVariable long id) {

		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for editing book");
		}

		Book prevBook = bookRepo.findById(id).get();

		prevBook.setAuthor(book.getAuthor());
		prevBook.setIsbn(book.getIsbn());
		prevBook.setTitle(book.getTitle());

		bookRepo.save(prevBook);
		Map<String, String> map = new HashMap<String, String>();
		map.put("status", "succes");
		return map;
	}

	@GetMapping("book/all")
	public List<Book> findAllBooks() {
		return bookRepo.findAll();
	}

	@GetMapping("book/all/user")
	public List<Book> findAllNonUserReservedBooks(@RequestHeader(name = "Authorization") String token) {

		if (!userController.userIsLoggedIn(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in");
		}

		long userId = userRepo.findByToken(token).getId();

		List<Book> books = bookRepo.findAll();
		List<Book> booksNotReservedByUser = new ArrayList<Book>();

		// Go over all 
		outerloop: for (Book book : books) {

			for (Reservation reservation : book.getReservations()) {

				long reservationUserId = reservation.getUser().getId();

				// Book has already been reserved by user if:
				if (reservationUserId == userId) {
					if (reservation.getStatus().equals("PENDING"))
						continue outerloop;
				}
			}

			booksNotReservedByUser.add(book);
		}

		return booksNotReservedByUser;
	}

	@GetMapping("book/{id}")
	public Book findBook(@PathVariable long id) {
		return bookRepo.findById(id).get();
	}
	
	@GetMapping("book/copy/{id}")
	public List<Copy> getAvailableCopiesById(@PathVariable long id) {
		
		List<Copy> copies = copyRepo.findAll();
		List<Copy> copiesOfBook = new ArrayList<Copy>();
		
		outerloop: 
		for (Copy copy : copies) {
			
			if (copy.getBook().getId() == id) {
				
				for (Loan loan : copy.getLoans()) {
					
					if (loan.getEndDate() == null || loan.getEndDate().equalsIgnoreCase(""))
						continue outerloop;
				}
				
				copiesOfBook.add(copy);
			}
		}
		
		return copiesOfBook;
	}
}
