package nl.workingtalent.bookrental.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import nl.workingtalent.bookrental.dto.NewUserDto;

@Service
public class ValidatePassword {
	
	private String ENTERED_PASSWORD;
	private String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

	
	public boolean passwordIsValid(NewUserDto newUserDto) {
		ENTERED_PASSWORD = newUserDto.getPassword();
		
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(ENTERED_PASSWORD);
        
        return matcher.matches();
	}
	
}
