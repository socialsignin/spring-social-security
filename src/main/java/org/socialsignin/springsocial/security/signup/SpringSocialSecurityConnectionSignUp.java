package org.socialsignin.springsocial.security.signup;

import org.socialsignin.springsocial.security.api.SpringSocialProfile;
import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class SpringSocialSecurityConnectionSignUp extends
		AbstractSpringSocialSecurityConnectionSignUp<
		SpringSocialProfile, SpringSocialSecuritySignUpService,SpringSocialSecurityProfileFactory> {
	
}
