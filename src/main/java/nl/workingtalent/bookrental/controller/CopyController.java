package nl.workingtalent.bookrental.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.model.Copy;
import nl.workingtalent.bookrental.model.Loan;
import nl.workingtalent.bookrental.repository.IBookRepository;
import nl.workingtalent.bookrental.repository.ICopyRepository;
import nl.workingtalent.bookrental.repository.IUserRepository;

@RestController
@CrossOrigin(maxAge = 3600)
public class CopyController {

	@Autowired
	private ICopyRepository copyRepo;
	@Autowired
	private IBookRepository bookRepo;
	@Autowired
	private IUserRepository userRepo;
	@Autowired
	private UserController userController;

	@PostMapping("copy/create/{bookId}")
	public Copy createCopy(@RequestHeader(name = "Authorization") String token, @PathVariable long bookId) {

		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for creating Copy");
		}

		Book book = bookRepo.findById(bookId).get();
		Copy copy = new Copy(book);

		book.addCopy(copy);

		copyRepo.save(copy);
		bookRepo.save(book);

		return copy;
	}

	@GetMapping("book/all/loaned")
	public List<Copy> getAllCurrentLoans(@RequestHeader(name = "Authorization") String token) {

		if (!userController.userIsLoggedIn(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in");
		}

		List<Copy> copies = copyRepo.findAll();
		List<Copy> loanedCopiesByUser = new ArrayList<Copy>();

		for (Copy copy : copies) {

			for (Loan loan : copy.getLoans()) {

				// Copy is being rented and does NOT have an end date
				if (loan.getStartDate() != null && !loan.getStartDate().equalsIgnoreCase("")) {
					if (loan.getEndDate() == null || loan.getEndDate().equalsIgnoreCase("")) {
						loanedCopiesByUser.add(copy);
					}
				}
			}
		}
		return loanedCopiesByUser;
	}

	@GetMapping("book/all/loaned/user")
	public List<Copy> getAllCurrentLoansByUser(@RequestHeader(name = "Authorization") String token) {

		if (!userController.userIsLoggedIn(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in");
		}

		long userId = userRepo.findByToken(token).getId();

		List<Copy> copies = copyRepo.findAll();
		List<Copy> loanedCopies = new ArrayList<Copy>();

		for (Copy copy : copies) {

			for (Loan loan : copy.getLoans()) {

				// See if id matches with user
				if (loan.getUser().getId() != userId)
					break;

				// Copy is being rented and does NOT have an end date
				if (loan.getStartDate() != null && !loan.getStartDate().equalsIgnoreCase("")) {
					if (loan.getEndDate() == null || loan.getEndDate().equalsIgnoreCase("")) {
						loanedCopies.add(copy);
					}
				}
			}
		}
		return loanedCopies;
	}
}
