package nl.workingtalent.bookrental.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

	@GetMapping("reservation/create/{bookId}/{userId}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Reservation createReservation(@RequestHeader(name = "Authorization") String token, @PathVariable long bookId,
			@PathVariable long userId) {

		// Check if user is trying to make a reservation for themselves.
		if (!userController.userIdMatches(token, userId)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Requested action affects other user(s)");
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
			@PathVariable long reservationId, @PathVariable long copyId, @PathVariable boolean toApprove) {

		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for approving reservation");
		}

		Reservation reservation = reservationRepo.findById(reservationId).get();

		reservation.setStatus(toApprove ? "APPROVED" : "DENIED");

		reservationRepo.save(reservation);

		// Only look for copy if the reservation is accepted
		if (toApprove) {

			Copy copy = copyRepo.findById(copyId).get();

			// Check if copy matches book
			if (reservation.getBook() != copy.getBook()) {
				// Book does not match copy
				throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Copy belongs to a different book");
			}

			// Check if copy is in service
			if (!copy.isInService())
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Copy is no longer in service");

			// Create loan
			// TODO: remove return and storing of loan, is unnecessary
			Loan loan = loanController.createLoan(token, copyId, reservation.getUser().getId());
			return loan;
		}

		throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Unexpected failure when creating loan");
	}

	@GetMapping("reservation/pending")
	public List<Reservation> getPendingReservations(@RequestHeader(name = "Authorization") String token) {

		if (!userController.userIsLoggedIn(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in");
		}

		List<Reservation> allReservations = new ArrayList<Reservation>();
		List<Reservation> pendingReservations = new ArrayList<Reservation>();

		allReservations = reservationRepo.findAll();

		for (Reservation reservation : allReservations) {
			if (reservation.getStatus().equalsIgnoreCase("PENDING")) {
				pendingReservations.add(reservation);
			}
		}

		System.out.print("Success");

		return pendingReservations;
	}
}
