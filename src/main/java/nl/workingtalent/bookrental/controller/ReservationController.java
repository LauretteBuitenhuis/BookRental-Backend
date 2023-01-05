package nl.workingtalent.bookrental.controller;

import java.io.Console;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping("reservation/create/{bookId}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Reservation createReservation(@RequestHeader(name = "Authorization") String token, @PathVariable long bookId) {

		long userId = userRepo.findByToken(token).getId();
		
		// Check if user is trying to make a reservation for themselves.
		if (!userController.userIsLoggedIn(token)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "User is not logged in");
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
	@PostMapping("reservation/approve/{reservationId}/{copyId}")
	public Loan updateReservationApproval(@RequestHeader(name = "Authorization") String token,
			@PathVariable long reservationId, @PathVariable long copyId) {

		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for approving reservation");
		}

		Reservation reservation = reservationRepo.findById(reservationId).get();

		reservation.setStatus("APPROVED");

		reservationRepo.save(reservation);

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

	@PutMapping("reservation/deny/{reservationId}")
	public Reservation updateReservationApproval(@RequestHeader(name = "Authorization") String token,
			@PathVariable long reservationId) {

		if (!userController.userIsAdmin(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid permissions for approving reservation");
		}

		Reservation reservation = reservationRepo.findById(reservationId).get();

		reservation.setStatus("DENIED");

		reservationRepo.save(reservation);
		
		return reservation;
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
