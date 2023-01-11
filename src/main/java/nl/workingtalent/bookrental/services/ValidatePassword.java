package nl.workingtalent.bookrental.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import nl.workingtalent.bookrental.dto.NewUserDto;

@Service
public class ValidatePassword {

	public boolean passwordIsValid(NewUserDto newUserDto) {
		String enteredPassword = newUserDto.getPassword();
		
		// Regex checks password for: 
		// lowercase letters [a-z], uppercase letters [A-Z], digits /d, special characters [@$!%*?&], minimum length {8,}
		String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
		
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(enteredPassword);
        
        return matcher.matches();
	}
	
}
