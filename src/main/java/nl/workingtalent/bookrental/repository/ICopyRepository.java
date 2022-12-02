package nl.workingtalent.bookrental.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import nl.workingtalent.bookrental.model.Copy;

public interface ICopyRepository extends JpaRepository<Copy, Long>{

}
