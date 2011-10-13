package org.socialsignin.springframework.social.security.userauthorities;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

/**
* A service that provides the granted authorities for a given userId
* Implemented by {@link SimpleUserAuthoritiesService}.
* @author Michael Lavelle
*/
public interface UserAuthoritiesService {

	public List<GrantedAuthority> getAuthoritiesForUser(String userId);
	
}
