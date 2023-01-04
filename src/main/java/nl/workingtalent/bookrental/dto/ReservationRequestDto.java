package nl.workingtalent.bookrental.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.model.User;

public class ReservationRequestDto {

	@JsonCreator
	public ReservationRequestDto(@JsonProperty("id") long id, @JsonProperty("status") String status, 
			@JsonProperty("book") Book book, @JsonProperty("user") User user) {
		super();
		this.id = id;
		this.status = status;
		this.book = book;
		this.user = user;
	}

	private final long id;
	private final String status;
	private final Book book;
	private final User user;
	
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
