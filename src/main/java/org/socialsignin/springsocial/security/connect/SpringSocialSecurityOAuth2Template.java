package org.socialsignin.springsocial.security.connect;

import org.springframework.social.oauth2.OAuth2Template;

public class SpringSocialSecurityOAuth2Template extends OAuth2Template {

	/**
	 * Pseudo OAuth2Template - do not require clientId,clientSecret or accessTokenUrl
	 */
	public SpringSocialSecurityOAuth2Template() {
		super("", "", null, "");	
	}
}
