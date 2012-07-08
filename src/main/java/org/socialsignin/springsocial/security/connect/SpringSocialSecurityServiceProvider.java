package org.socialsignin.springsocial.security.connect;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.api.impl.SpringSocialSecurityTemplate;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ConnectionData;

public class SpringSocialSecurityServiceProvider implements
		ServiceProvider<SpringSocialSecurity> {

	public SpringSocialSecurity getSpringSocialSecurity(
			ConnectionData connectionData) {
		return new SpringSocialSecurityTemplate(connectionData);
	}

}
