package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	private LoanController loanController;

	// TODO: Change to status return, does not need to return anything at all
	@GetMapping("reservation/create/{bookId}/{userId}")
	public Reservation createReservation(@PathVariable long bookId, @PathVariable long userId) {
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
	public Loan updateReservationApproval(@PathVariable long reservationId, @PathVariable long copyId,
			@PathVariable boolean toApprove) {
		Reservation reservation = reservationRepo.findById(reservationId).get();

		reservation.setStatus(toApprove ? "APPROVED" : "DENIED");

		reservationRepo.save(reservation);
		
		if (toApprove) {
			// Create loan
			// TODO: remove return and storing of loan, is unnecessary
			Loan loan = loanController.createLoan(copyId, reservation.getUser().getId());
			return loan;
		}

		return null;
	}
}
