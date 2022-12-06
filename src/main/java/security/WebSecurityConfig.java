//class that gives a security configuration such that you can only enter certain pages after logging in

package security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity //annotation is used to enable Spring Security’s web security support and provide the Spring MVC integration.
public class WebSecurityConfig 	{

}
