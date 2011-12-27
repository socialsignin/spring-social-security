package org.socialsignin.springframework.social.security.signin;

public class NonUniqueConnectionException extends RuntimeException {

	public NonUniqueConnectionException(String message)
	{
		super(message);
	}
}

