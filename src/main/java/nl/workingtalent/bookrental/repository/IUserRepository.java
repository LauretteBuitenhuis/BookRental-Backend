package nl.workingtalent.bookrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.workingtalent.bookrental.model.User;

public interface IUserRepository extends JpaRepository<User, Long>{
	User findByEmail(String email); // find the data belonging to the entered email address
}
