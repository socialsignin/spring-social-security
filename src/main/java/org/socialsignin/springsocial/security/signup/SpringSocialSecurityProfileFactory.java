package org.socialsignin.springsocial.security.signup;

import org.socialsignin.springsocial.security.api.SpringSocialProfile;
import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;

import org.springframework.stereotype.Service;

@Service
public class SpringSocialSecurityProfileFactory extends
		AbstractSpringSocialProfileFactory<SpringSocialProfile> {


	@Override
	public SpringSocialProfile instantiate() {
		return new SpringSocialSecurityProfile();
	}


}
