package nl.workingtalent.bookrental.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import nl.workingtalent.bookrental.dto.NewUserDto;

@Service
public class ValidatePassword {
	
	private String ENTERED_PASSWORD;
	// TODO - add special character
	// TODO - add minimum length
	private String REGEX = "^(?=.*\\p{Upper})(?=.*[a-zA-Z])(?=.*[0-9]).*[a-zA-Z0-9]*$";
	
	public boolean passwordIsValid(NewUserDto newUserDto) {
		ENTERED_PASSWORD = newUserDto.getPassword();
		
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(ENTERED_PASSWORD);
        
        return matcher.matches();
	}
	
}
