package org.socialsignin.springsocial.security.signup;

import org.springframework.social.SocialException;

public class UsernameAlreadyExistsException extends SocialException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UsernameAlreadyExistsException(String userId) {
		super("A user account with username:" + userId + " already exists");
	}

}
