package nl.workingtalent.bookrental.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Copy {

	public Copy() {}
	
	public Copy(Book book) {
		super();
		this.book = book;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private boolean inService = true;

	@ManyToOne(optional = false)
	private Book book;

	@JsonIgnore
	@OneToMany(mappedBy = "copy")
	private List<Loan> loans;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isInService() {
		return inService;
	}

	public void setInService(boolean inService) {
		this.inService = inService;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public List<Loan> getLoans() {
		return loans;
	}

	public void addLoan(Loan loan) {
		this.loans.add(loan);
	}
	
	public void removeLoan(Loan loan) {
		this.loans.remove(loan);
	}
}
