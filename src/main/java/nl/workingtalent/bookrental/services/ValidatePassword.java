package nl.workingtalent.bookrental.services;

import nl.workingtalent.bookrental.dto.NewUserDto;

public class ValidatePassword {
	
	private String ENTERED_PASSWORD;
	private String ALLOWED_CHARS = "1234567890";
	
	public boolean passwordIsValid(NewUserDto newUserDto) {
		ENTERED_PASSWORD = newUserDto.getPassword();
		
		return true;
	}

}
