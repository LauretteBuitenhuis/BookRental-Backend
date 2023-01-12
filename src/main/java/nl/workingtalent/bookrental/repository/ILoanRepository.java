package nl.workingtalent.bookrental.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import nl.workingtalent.bookrental.model.Loan;

public interface ILoanRepository extends JpaRepository<Loan, Long>{

	public Loan findById(long id);
	
}
