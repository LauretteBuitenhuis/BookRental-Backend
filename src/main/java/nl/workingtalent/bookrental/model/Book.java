package nl.workingtalent.bookrental.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Book {
	public Book() {
		// TODO Spring need this Default constructor. Add some status code?
	}

	public Book(String title, String author, String isbn) {
		this.title = title;
		this.author = author;
		this.isbn = isbn;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false, length = 100)
	private String title;
	
	@Column(nullable = false, length = 100)
	private String author;
	
	@Column(nullable = false, length = 20)
	private String isbn;
	
	@JsonIgnore
	@OneToMany(mappedBy = "book")
	private List<Tag> tags  = new ArrayList<Tag>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "book")
	private List<Copy> copies = new ArrayList<Copy>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "book")
	private List<Reservation> reservations  = new ArrayList<Reservation>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<Copy> getCopies() {
		return copies;
	}

	public void addCopy(Copy copy) {
		this.copies.add(copy);
	}
	
	public void removeCopy(Copy copy) {
		this.copies.remove(copy);
	}

	public List<Reservation> getReservations() {
		return reservations;
	}

	public void addReservation(Reservation reservation) {
		this.reservations.add(reservation);
	}
	
	public void removeReservation(Reservation reservation) {
		this.reservations.remove(reservation);
	}
}
