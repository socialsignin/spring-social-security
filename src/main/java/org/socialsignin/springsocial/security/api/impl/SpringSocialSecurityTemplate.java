package org.socialsignin.springsocial.security.api.impl;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.springframework.social.connect.ConnectionData;

public class SpringSocialSecurityTemplate implements SpringSocialSecurity {

	private ConnectionData connectionData;

	public SpringSocialSecurityTemplate(ConnectionData connectionData) {
		this.connectionData = connectionData;
	}

	@Override
	public SpringSocialSecurityProfile getUserProfile() {

		return new SpringSocialSecurityProfile(
				connectionData.getProviderUserId(),
				connectionData.getAccessToken(),
				connectionData.getDisplayName(),
				connectionData.getImageUrl(),
				connectionData.getProfileUrl());
	}

}
