//class that gives a security configuration such that you can only enter certain pages after logging in

package security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;



@Configuration
@EnableWebSecurity //annotation is used to enable Spring Security’s web security support and provide the Spring MVC integration.
public class WebSecurityConfig 	{
	
	// sets up an in-memory user store with a single user.
	@Autowired
	private UserDetailsService userDetailsService() {
		return new CumstomUserDetailsService();
	}
	
	// Fetch user info from database (DAO: data access object)
	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); // provides us to talk with the database
		provider.setUserDetailsService(userDetailsService());
		// later on; implement password encoder here
		provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
		return provider;
	}
	

}
