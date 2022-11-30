package nl.workingtalent.bookrental.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import nl.workingtalent.bookrental.model.Book;

public interface IBookRepository extends JpaRepository<Book, Long>{

}
