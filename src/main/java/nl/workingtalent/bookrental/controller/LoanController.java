package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

	@PostMapping("loan/create/{copyId}/{userId}")
	public Loan createLoan(@RequestHeader(name = "Authorization") String token, @PathVariable long copyId,
			@PathVariable long userId) {

		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for creating Loan");
		}

		// Get copy and user information from ids
		Copy copy = copyRepo.findById(copyId).get();
		User user = userRepo.findById(userId).get();

		// Create new loan
		Loan loan = new Loan(getCurrentDate(), user, copy);

		// Assign loan to user and to the copy
		user.addLoan(loan);
		copy.addLoan(loan);

		// Update database to include new loan, and changes to other tables
		copyRepo.save(copy);
		userRepo.save(user);
		loanRepo.save(loan);

		return loan;
	}
	
	private String getCurrentDate() {
		// Get current date
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now);
		
		return date;
	}

	@GetMapping("loan/history")
	public List<Loan> getUserLoanHistory(@RequestHeader(name = "Authorization") String token) {

		if (!userController.userIsLoggedIn(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in");
		}

		long userId = userRepo.findByToken(token).getId();

		List<Loan> allLoans = loanRepo.findAll();
		List<Loan> userPastLoans = new ArrayList<Loan>();

		for (Loan loan : allLoans) {

			// See if id matches with user
			if (loan.getUser().getId() != userId)
				continue;

			// Copy is being rented and does NOT have an end date
			if (loan.getStartDate() != null && !loan.getStartDate().equalsIgnoreCase("")) {
				if (loan.getEndDate() != null && !loan.getEndDate().equalsIgnoreCase("")) {
					userPastLoans.add(loan);
				}
			}
		}

		return userPastLoans;
	}

	@PostMapping("loan/end/{loanId}")
	public Loan endLoanWithId(@RequestHeader(name = "Authorization") String token, @PathVariable long loanId) {

		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions");
		}

		Loan loan = loanRepo.findById(loanId);
		
		loan.setEndDate(getCurrentDate());
		loanRepo.save(loan);

		return loan;
	}
}
