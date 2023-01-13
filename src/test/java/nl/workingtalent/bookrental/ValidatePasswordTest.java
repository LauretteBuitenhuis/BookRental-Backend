package nl.workingtalent.bookrental;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import nl.workingtalent.bookrental.services.ValidatePassword;

class ValidatePasswordTest {

	@Test
	void checkAValidPassword() {
		ValidatePassword validator = new ValidatePassword();
		boolean result = validator.passwordIsValid("Welkom123!");
		assertTrue(result);
	}
	
	@Test
	void checkAnInvalidPassword() {
		ValidatePassword validator = new ValidatePassword();
		boolean result = validator.passwordIsValid("Welkom123");
		assertFalse(result);
	}
}
