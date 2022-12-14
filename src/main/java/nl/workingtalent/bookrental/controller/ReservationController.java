package nl.workingtalent.bookrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import nl.workingtalent.bookrental.model.Book;
import nl.workingtalent.bookrental.model.Copy;
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

	@PostMapping("reservation/create/{bookId}/{userId}")
	public void createCopy(@PathVariable long bookId, @PathVariable long userId) {
		Book book = bookRepo.findById(bookId).get();
		User user = userRepo.findById(userId).get();
		Reservation reservation = new Reservation();

		book.addReservation(reservation);
		reservation.setBook(book);
		reservation.setUser(user);

		bookRepo.save(book);
		reservationRepo.save(reservation);
	}

	@PostMapping("reservation/approve/{reservationId}/{copyId}/{toApprove}")
	public void updateReservationApproval(@PathVariable long reservationId, @PathVariable long copyId,
			@PathVariable boolean toApprove) {
		Reservation reservation = reservationRepo.findById(reservationId).get();

		reservation.setApproved(toApprove);

		if (toApprove) {
			// Create loan
			loanController.createLoan(copyId, reservation.getUser().getId());
		}

		// TODO: remove reservation from database
		reservationRepo.save(reservation);
	}
}
