package org.socialsignin.springframework.social.security.signin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUserIdHolder {

	public static String getAuthenticatedUserId()
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null || auth.getName().equals("anonymousUser") ? null :auth.getName();
	}
	
}
