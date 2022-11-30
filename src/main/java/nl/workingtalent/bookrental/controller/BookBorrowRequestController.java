package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.model.BookBorrowRequest;
import nl.workingtalent.bookrental.repository.IBookRepository;

@RestController
public class BookBorrowRequestController {
	
	@Autowired
	private IBookRepository bookRepository;

	// Voorbeeld code om objecten aan elkaar te koppelen
	public void createBookBorrowRequest() {
		// Haal book uit de database
		Book book = bookRepository.findById(1L).get();
		
		BookBorrowRequest bb = new BookBorrowRequest();
		bb.setBook(book);
		
		// Book borrow request save
		
	}
	
}
