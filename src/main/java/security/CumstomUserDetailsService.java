// This class tells Spring security how to look up the user information
package security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import nl.workingtalent.bookrental.model.User;
import nl.workingtalent.bookrental.repository.IUserRepository;

@Service
public class CumstomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private IUserRepository repo;
	
	// Authenticate the user, and if successful, a new object of type customUserDetails 
	//is created to represents the authenticated user
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = repo.findByEmail(email);
		if(user == null) // Throw message if email is not in database
			throw new UsernameNotFoundException("Emailadress not found, try again please");
		return new CustomUserDetails(user);
	}

} 
