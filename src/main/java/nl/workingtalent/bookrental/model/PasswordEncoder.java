package nl.workingtalent.bookrental.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.security.SecureRandom;


public class PasswordEncoder {
	
	public String encode(String plainPassword) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10, new SecureRandom());
		return encoder.encode(plainPassword);
		}
	
}
