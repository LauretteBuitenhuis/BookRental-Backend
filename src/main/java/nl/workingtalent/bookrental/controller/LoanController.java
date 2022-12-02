package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
	public void createLoan(@RequestBody Loan loan, @PathVariable long copyId, @PathVariable long userId) {
		Copy copy = copyRepo.findById(copyId).get();
		User user = userRepo.findById(userId).get();
		loan.setCopy(copy);
		loan.setUser(user);
		loanRepo.save(loan);
	}
}
