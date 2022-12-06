package nl.workingtalent.bookrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.workingtalent.bookrental.model.User;

public interface IUserRepository extends JpaRepository<User, Long>{
	User findByEmail(String email);
	User findByPassword(String password);
}
