package nl.workingtalent.bookrental.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Book {
	public Book() {}

	public Book(String title, String author, String isbn) {
		this.title = title;
		this.author = author;
		this.isbn = isbn;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="book_id")
	private long id;
	
	@Column(nullable = false, length = 100)
	private String title;
	
	@Column(nullable = false, length = 100)
	private String author;
	
	@Column(nullable = false, length = 20)
	private String isbn;
	
	@JoinTable(name="tag_book", joinColumns=@JoinColumn(name="book_id"),inverseJoinColumns=@JoinColumn(name="tag_id"))
	@ManyToMany(cascade= {CascadeType.MERGE,CascadeType.REFRESH,CascadeType.DETACH,CascadeType.PERSIST})
	private Set<Tag> tags = new HashSet<Tag>();
	
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

	public Set<Tag> getTags() {
		return tags;
	}

	public void addTag(Tag tag) {
		this.tags.add(tag);
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
