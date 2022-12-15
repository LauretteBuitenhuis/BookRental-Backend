package nl.workingtalent.bookrental.services;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import nl.workingtalent.bookrental.dto.NewUserDto;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender emailSender;
	
	public void sendEmail(NewUserDto newUserDto) {
		
		// TODO - create front end 8082/user/activate
		String activationUrl = "http://localhost:8082/user/activate";
		String from = "no-reply@workingtalent.nl";
		String to = newUserDto.getEmail();
		String firstName = newUserDto.getFirstName();
		String password = newUserDto.getPassword();
		
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		try {
			helper.setSubject("Activeer je Book Rental account");
			helper.setFrom(from);
			helper.setTo(to);
			
			String content = String.format("Hi %s,<br>Welkom bij de Book Rental van Working Talent!<br>"
					+ "Voordat je aan de slag kunt, zul je eerst je account moeten activeren via de onderstaande link.<br>"
					+ "Log in met jouw email en wachtwoord: %s<br>"
					+ "<i><a href = %s>Activeer</a></i>", 
					firstName, password, activationUrl);
			
			helper.setText(content, true);
			
			emailSender.send(message);	
		} 
		catch (MessagingException error) { System.out.print(error); }
	}	
}