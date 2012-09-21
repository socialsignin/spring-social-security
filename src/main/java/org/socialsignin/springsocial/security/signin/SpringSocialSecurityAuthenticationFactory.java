/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * Encapsulates the logic of creating/updating local user authentication
 * on the basis of provider-specific ROLE scheme.
 * 
 * @author Michael Lavelle
 */
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

	public Authentication updateAuthenticationForNewProvider(
			Authentication existingAuthentication, String providerId) {
		return createNewAuthentication(
				existingAuthentication.getName(),
				existingAuthentication.getCredentials().toString(),
				addAuthority(existingAuthentication, userAuthoritiesService
						.getProviderAuthority(providerId)));
	}
	
	public Authentication updateAuthenticationForNewProviders(
			Authentication existingAuthentication, Set<String> providerIds) {
		List<GrantedAuthority> newAuthorities = new ArrayList<GrantedAuthority>();
		for (String providerId : providerIds)
		{
			newAuthorities.add(userAuthoritiesService.getProviderAuthority(providerId));
		}
		return createNewAuthentication(
				existingAuthentication.getName(),
				existingAuthentication.getCredentials().toString(),
				addAuthorities(existingAuthentication, newAuthorities));
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
	
	private Collection<? extends GrantedAuthority> addAuthorities(
			Authentication authentication, Collection<GrantedAuthority> newAuthorities) {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.addAll(authentication.getAuthorities());
		if (newAuthorities != null) {
			for (GrantedAuthority newAuthority : newAuthorities)
			{
				if (!authorities.contains(newAuthority)) {
					authorities.add(newAuthority);
				}
			}
		}

		return authorities;
	}
	
	public void setIncludeExpiredConnectionsInAuthorisations(
			boolean includeExpiredConnectionsInAuthorisations) {
		this.includeExpiredConnectionsInAuthorisations = includeExpiredConnectionsInAuthorisations;
	}



}
