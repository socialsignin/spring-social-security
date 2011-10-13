package org.socialsignin.springframework.social.security.userauthorities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/**
* Simple implementation of UserAuthoritiesService, by default assigning each user the same role
* @author Michael Lavelle
*/
@Service
public class SimpleUserAuthoritiesService implements UserAuthoritiesService {

	private String defaultAuthorityName = "ROLE_USER";
	
	public void setDefaultAuthorityName(String defaultAuthorityName) {
		this.defaultAuthorityName = defaultAuthorityName;
	}

	protected List<GrantedAuthority> getDefaultAuthorities()
	{
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority(defaultAuthorityName));
		return grantedAuthorities;
	}
	
	@Override
	public List<GrantedAuthority> getAuthoritiesForUser(String userId) {
		return getDefaultAuthorities();
	}

}
