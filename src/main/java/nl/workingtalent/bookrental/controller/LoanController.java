package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
	
	@Autowired 
	private UserController userController;
	
	// TODO: Change to status return, does not need to return anything at all
	@PostMapping("loan/create/{copyId}/{userId}")
	public Loan createLoan(@RequestHeader(name = "Authorization") String token, @PathVariable long copyId, @PathVariable long userId) {	
		
		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for creating Loan");
		}
		
		// Get copy and user information from ids
		Copy copy = copyRepo.findById(copyId).get();
		User user = userRepo.findById(userId).get();

		// Get current date
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now);

		// Create new loan
		Loan loan = new Loan(date, user, copy);

		// Assign loan to user and to the copy
		user.addLoan(loan);
		copy.addLoan(loan);
	
		// Update database to include new loan, and changes to other tables
		copyRepo.save(copy);
		userRepo.save(user);
		loanRepo.save(loan);
		
		return loan;
	}
	
	// TODO: Remove loan upon handing in book
	// Set end date
	// Remove from user list
	// Remove from database, store in history table
}
