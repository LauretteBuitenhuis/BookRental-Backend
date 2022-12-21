package nl.workingtalent.bookrental.dto;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.model.User;

public class ReservationRequestDto {

	public ReservationRequestDto(int id, String status, Book book, User user) {
		super();
		this.id = id;
		this.status = status;
		this.book = book;
		this.user = user;
	}

	private int id;
	private String status;
	private Book book;
	private User user;
	
	public int getId() {
		return id;
	}
	
	public String getStatus() {
		return status;
	}
	
	public Book getBook() {
		return book;
	}
	

	public User getUser() {
		return user;
	}	
}
