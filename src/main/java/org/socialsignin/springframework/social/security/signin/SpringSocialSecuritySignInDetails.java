package org.socialsignin.springframework.social.security.signin;

import org.springframework.social.connect.Connection;

/**
* POJO for holding userid and connection, set in session by SpringSocialSecuritySignInService
* and retrieved from session in SpringSocialSecurityAuthenticationFilter
* 
* @author Michael Lavelle
*/
public class SpringSocialSecuritySignInDetails {

	public String getUserId() {
		return userId;
	}

	public Connection<?> getConnection() {
		return connection;
	}

	private String userId;
	private Connection<?> connection;
	
	public SpringSocialSecuritySignInDetails(String userId,Connection<?> connection)
	{
		this.userId = userId;
		this.connection = connection;
	}
	
}
