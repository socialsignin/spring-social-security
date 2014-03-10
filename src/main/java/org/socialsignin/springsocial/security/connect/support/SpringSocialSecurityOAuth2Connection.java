package org.socialsignin.springsocial.security.connect.support;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.connect.SpringSocialSecurityServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2Connection;

public class SpringSocialSecurityOAuth2Connection extends OAuth2Connection<SpringSocialSecurity> {

	private static final long serialVersionUID = 1L;
	
	private SpringSocialSecurityServiceProvider serviceProvider;

	
	public SpringSocialSecurityOAuth2Connection(ConnectionData data,
			SpringSocialSecurityServiceProvider serviceProvider,
			ApiAdapter<SpringSocialSecurity> apiAdapter) {
		super(data, serviceProvider, apiAdapter);
		this.serviceProvider = serviceProvider;
	}
	
	@Override
	public SpringSocialSecurity getApi() {
		return serviceProvider.getSpringSocialSecurity(createData());
	}

}
