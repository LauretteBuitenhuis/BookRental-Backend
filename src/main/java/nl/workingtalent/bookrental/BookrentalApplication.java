package nl.workingtalent.bookrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nl.workingtalent.bookrental.model.GeneratePassword;

@SpringBootApplication
public class BookrentalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookrentalApplication.class, args);
	}
}
