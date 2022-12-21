package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.model.Copy;
import nl.workingtalent.bookrental.model.Loan;
import nl.workingtalent.bookrental.model.Reservation;
import nl.workingtalent.bookrental.model.User;
import nl.workingtalent.bookrental.repository.IBookRepository;
import nl.workingtalent.bookrental.repository.ICopyRepository;
import nl.workingtalent.bookrental.repository.IReservationRepository;
import nl.workingtalent.bookrental.repository.IUserRepository;

@RestController
@CrossOrigin(maxAge = 3600)
public class ReservationController {

	@Autowired
	private IReservationRepository reservationRepo;
	@Autowired
	private IBookRepository bookRepo;
	@Autowired
	private IUserRepository userRepo;
	@Autowired
	private ICopyRepository copyRepo;
	@Autowired
	private LoanController loanController;
	@Autowired
	private UserController userController;

	// TODO: Change to status return, does not need to return anything at all
	@GetMapping("reservation/create/{bookId}/{userId}")
	public Reservation createReservation(@RequestHeader(name = "Authorization") String token, 
			@PathVariable long bookId, @PathVariable long userId) {
		
		// Check if user is trying to make a reservation for themselves.
		if (!userController.CheckUserId(token, userId)) {
			return null; // TODO: Change to status return
		}
		
		Book book = bookRepo.findById(bookId).get();
		User user = userRepo.findById(userId).get();
		Reservation reservation = new Reservation("PENDING", book, user);
		
		book.addReservation(reservation);

		bookRepo.save(book);
		reservationRepo.save(reservation);
		
		return reservation;
	}

	// TODO: Change to status return, does not need to return anything at all
	@GetMapping("reservation/approve/{reservationId}/{copyId}/{toApprove}")
	public Loan updateReservationApproval(@RequestHeader(name = "Authorization") String token, 
			@PathVariable long reservationId, @PathVariable long copyId,
			@PathVariable boolean toApprove) {
		
		// TODO: Change to return status object instead
		if (!userController.CheckUserPermissions(token)) return null;
		
		Reservation reservation = reservationRepo.findById(reservationId).get();
		
		// Check if copy matches book
		if (reservation.getBook() != copyRepo.findById(copyId).get().getBook()) {
			// Book does not match copy
			return null; // TODO: Change to status return
		}

		reservation.setStatus(toApprove ? "APPROVED" : "DENIED");

		reservationRepo.save(reservation);
		
		if (toApprove) {
			// Create loan
			// TODO: remove return and storing of loan, is unnecessary
			Loan loan = loanController.createLoan(token, copyId, reservation.getUser().getId());
			return loan;
		}

		return null;
	}
}
