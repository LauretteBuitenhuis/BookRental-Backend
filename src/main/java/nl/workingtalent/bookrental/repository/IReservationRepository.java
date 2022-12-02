package nl.workingtalent.bookrental.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import nl.workingtalent.bookrental.model.Reservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long>{

}
