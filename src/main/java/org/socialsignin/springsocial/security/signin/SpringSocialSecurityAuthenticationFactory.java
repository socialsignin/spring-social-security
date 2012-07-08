package org.socialsignin.springsocial.security.signin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.socialsignin.springsocial.security.userauthorities.UserAuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.stereotype.Service;

@Service
public class SpringSocialSecurityAuthenticationFactory {

	@Autowired
	private UserAuthoritiesService userAuthoritiesService;
	
	private boolean includeExpiredConnectionsInAuthorisations = true;

	public void setUserAuthoritiesService(
			UserAuthoritiesService userAuthoritiesService) {
		this.userAuthoritiesService = userAuthoritiesService;
	}

	protected Authentication createNewAuthentication(String userId,
			String password, Collection<? extends GrantedAuthority> authorities) {
		return new UsernamePasswordAuthenticationToken(userId, password,
				authorities);
	}

	public Authentication createAuthenticationForAllConnections(
			String userName, String password, List<Connection<?>> connections) {
		return createNewAuthentication(userName, password,
				userAuthoritiesService.getAuthoritiesForUser(
						toConnectionKeySet(connections,includeExpiredConnectionsInAuthorisations), userName));
	}

	public Authentication updateAuthenticationForNewConnection(
			Authentication existingAuthentication, Connection<?> connection) {
		return createNewAuthentication(
				existingAuthentication.getName(),
				existingAuthentication.getCredentials().toString(),
				addAuthority(existingAuthentication, userAuthoritiesService
						.getProviderAuthority(connection.getKey())));
	}
	
	
	public Authentication createAuthenticationFromUserDetails(
			UserDetails userDetails) {
		return createNewAuthentication(userDetails.getUsername(),
				userDetails.getPassword(), userDetails.getAuthorities());
	}

	private Set<ConnectionKey> toConnectionKeySet(
			List<Connection<?>> connections,boolean includeExpiredConnections) {
		Set<ConnectionKey> connectionKeys = new HashSet<ConnectionKey>();
		for (Connection<?> connection : connections) {
			if (includeExpiredConnections || !connection.hasExpired()) {

				ConnectionData connectionData = connection.createData();
				connectionKeys.add(new ConnectionKey(connectionData
						.getProviderId(), connectionData.getProviderUserId()));
			}
		}
		return connectionKeys;
	}

	private Collection<? extends GrantedAuthority> addAuthority(
			Authentication authentication, GrantedAuthority newAuthority) {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.addAll(authentication.getAuthorities());
		if (newAuthority != null) {
			if (!authorities.contains(newAuthority)) {
				authorities.add(newAuthority);
			}
		}

		return authorities;
	}
	
	public void setIncludeExpiredConnectionsInAuthorisations(
			boolean includeExpiredConnectionsInAuthorisations) {
		this.includeExpiredConnectionsInAuthorisations = includeExpiredConnectionsInAuthorisations;
	}



}
