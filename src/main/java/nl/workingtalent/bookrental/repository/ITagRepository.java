package nl.workingtalent.bookrental.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import nl.workingtalent.bookrental.model.Tag;

public interface ITagRepository extends JpaRepository<Tag, Long>{

}
