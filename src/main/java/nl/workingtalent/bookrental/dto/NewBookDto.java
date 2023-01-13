package nl.workingtalent.bookrental.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NewBookDto {

	private final String title;	
	private final String author;	
	private final String isbn;

	
	@JsonCreator
	public NewBookDto(@JsonProperty("title") String title, @JsonProperty("author") String author, 
	@JsonProperty("isbn") String isbn) {
		this.title = title;
		this.author = author;
		this.isbn = isbn;
	}
	
	public String getTitle() {
		return title;
	}


	public String getAuthor() {
		return author;
	}


	public String getIsbn() {
		return isbn;
	}
	
}


