package org.socialsignin.springframework.social.security.signin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.socialsignin.springframework.social.security.userauthorities.UserAuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

public class SpringSocialSecurityConnectInterceptor<S> implements
		ConnectInterceptor<S> {
	
	@Autowired
	private UserAuthoritiesService userAuthoritiesService;
	
	@Override
	public void postConnect(Connection<S> connection, WebRequest webRequest) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> existingAuthorities = authentication.getAuthorities();
		GrantedAuthority newAuthority = userAuthoritiesService.getProviderAuthority(connection.getKey().getProviderId());

		if (!existingAuthorities.contains(newAuthority))
		{
			Set<String> providerIds =new HashSet<String>();
			providerIds.add(connection.getKey().getProviderId());
			List<GrantedAuthority> newAuthorities = new ArrayList<GrantedAuthority>();
			newAuthorities.add(newAuthority);
			newAuthorities.addAll(existingAuthorities);
			Authentication newAuthentication = new UsernamePasswordAuthenticationToken(AuthenticatedUserIdHolder.getAuthenticatedUserId(), null,newAuthorities);		
			SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		}
	}

	@Override
	public void preConnect(ConnectionFactory<S> arg0,
			MultiValueMap<String, String> arg1, WebRequest arg2) {
		
	}

}
