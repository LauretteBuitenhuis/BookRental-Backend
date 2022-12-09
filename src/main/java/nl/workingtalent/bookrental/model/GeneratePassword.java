package nl.workingtalent.bookrental.model;

import java.security.SecureRandom;
import java.util.Random;

public class GeneratePassword {
	
	private final String INPUT ="ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz_-+!@";
	private final Random RANDOM = new SecureRandom();
	
	public String generateRandomString(int length) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < length; i++) {
			builder.append(INPUT.charAt(RANDOM.nextInt(INPUT.length())));
		}
		return builder.toString();
	}
	
}



	


