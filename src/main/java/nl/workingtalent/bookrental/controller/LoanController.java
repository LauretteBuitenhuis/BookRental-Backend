package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

import nl.workingtalent.bookrental.model.Copy;
import nl.workingtalent.bookrental.model.Loan;
import nl.workingtalent.bookrental.model.User;
import nl.workingtalent.bookrental.repository.ICopyRepository;
import nl.workingtalent.bookrental.repository.ILoanRepository;
import nl.workingtalent.bookrental.repository.IUserRepository;

@RestController
@CrossOrigin(maxAge = 3600)
public class LoanController {
	
	@Autowired
	private ILoanRepository loanRepo;
	@Autowired
	private ICopyRepository copyRepo;
	@Autowired
	private IUserRepository userRepo;
	
	@PostMapping("loan/create/{copyId}/{userId}")
	public void createLoan(@PathVariable long copyId, @PathVariable long userId) {	
		
		// Get copy and user information from ids
		Copy copy = copyRepo.findById(copyId).get();
		User user = userRepo.findById(userId).get();

		// Get current date
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now);

		// Create new loan
		Loan loan = new Loan();
		loan.setCopy(copy);
		loan.setUser(user);
		loan.setStartDate(date);

		// Assign loan to user
		user.addLoan(loan);

		// Update user in database to include new loan
		userRepo.save(user);

		// Store loan in database
		loanRepo.save(loan);
	}
	
	// TODO: Remove loan upon handing in book
	// Set end date
	// Remove from user list
	// Remove from database, store in history table
}
