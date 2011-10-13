package org.socialsignin.springframework.social.security.signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;


/**
* A command that signs up a new user in the event no local user id could be mapped from a Connection. 
* 
* In SpringSocialSecurity, by default there is no need for a local user account to be created outside of spring social -
* by default just checks and returns user id associated with third party connection.
* 
* Returns null if user id already exists locally for a different connection
*
* @author Michael Lavelle
*/
@Service
public class SpringSocialSecurityConnectionSignUp implements ConnectionSignUp {

	
	@Autowired
	@Qualifier("springSocialSecurityUserDetailsService")
	private UserDetailsService springSocialSecurityUserDetailsService;
	

    public SpringSocialSecurityConnectionSignUp() {
    }

    public String execute(Connection<?> connection) {
        UserProfile userProfile = connection.fetchUserProfile();
        String userId = createAccount(userProfile);
        return userId != null ? userId: null;
    }
    
    public String generateUserId(UserProfile userProfile)
    {
    	return userProfile.getUsername();
    }

    private String createAccount(UserProfile userProfile) {
        if (userProfile == null || userProfile.getUsername() == null) {
            return null;
        }
        try
        {
        	springSocialSecurityUserDetailsService.loadUserByUsername(userProfile.getUsername());
        	return null;
        }
        catch (UsernameNotFoundException e)
        {
        	return generateUserId(userProfile);
        }

    }
	
}
