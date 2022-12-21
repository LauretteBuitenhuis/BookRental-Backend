package nl.workingtalent.bookrental.dto;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.model.User;

public class ReservationSendDto {

	public ReservationSendDto(String status, Book book, User user) {
		super();
		this.status = status;
		this.book = book;
		this.user = user;
	}
	
	private long id;
	private String status;
	private Book book;
	private User user;

	public void setId(long id) {
		this.id = id;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public long getId() {
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
