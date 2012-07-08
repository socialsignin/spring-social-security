package org.socialsignin.springsocial.security.signup;

import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.springframework.web.context.request.WebRequest;

public interface SignUpService {

	public void signUpUser(
			SpringSocialSecurityProfile springSocialSecurityProfile) throws UsernameAlreadyExistsException;

	public void signUpUserAndCompleteConnection(
			SpringSocialSecurityProfile springSocialSecurityProfile,
			WebRequest webRequest) throws UsernameAlreadyExistsException;

	public boolean isUserIdAvailable(String userId);
	
	public SpringSocialSecurityProfile getUserProfile(String userId);

}
