/*
 * Copyright 2011 the original author or authors.
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
package org.socialsignin.springsocial.security.userdetails;

import java.util.ArrayList;
import java.util.List;

import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.socialsignin.springsocial.security.signin.SpringSocialSecurityAuthenticationFactory;
import org.socialsignin.springsocial.security.signup.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

/**
 * UserDetailsService for SpringSocialSecurity - retrieves user details from
 * Spring Social connection repository
 * 
 * @author Michael Lavelle
 */
@Repository
@Service
@Transactional(readOnly = true)
@Qualifier("springSocialSecurityUserDetailsService")
public class SpringSocialSecurityUserDetailsService implements
		UserDetailsService {

	@Autowired
	private UsersConnectionRepository usersConnectionRepository;

	@Autowired
	private SpringSocialSecurityAuthenticationFactory authenticationFactory;
	
	@Autowired
	private SignUpService signUpService;
	
	/**
	 * Uses a <code>SignUpService</code> implementation to check if a local user account for this username is available 
	 * and if so, bases the user's authentication on the set of connections the user currently has to
	 * 3rd party providers.  Allows provider-specific roles to be set for each user - uses a <code>UsersConnectionRepository</code>
	 * to obtain list of connections the user has and a <code>SpringSocialSecurityAuthenticationFactory</code>
	 * to obtain an authentication based on those connections.
	 * 
	 */
	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		ConnectionRepository connectionRepository = usersConnectionRepository
				.createConnectionRepository(userName);
		SpringSocialSecurityProfile springSocialSecurityProfile = signUpService.getUserProfile(userName);
		List<Connection<?>> allConnections = getConnections(connectionRepository,userName);
		if (allConnections.size() > 0) {
				
				Authentication authentication = authenticationFactory
						.createAuthenticationForAllConnections(userName,
								springSocialSecurityProfile.getPassword(),
								allConnections);
				return new User(userName, authentication.getCredentials()
						.toString(), true, true, true, true,
						authentication.getAuthorities());
	
		} else {
			throw new UsernameNotFoundException(userName);
		}

	}
	

	private List<Connection<?>> getConnections(ConnectionRepository connectionRepository,String userName)
	{
		MultiValueMap<String, Connection<?>> connections = connectionRepository
		.findAllConnections();
		List<Connection<?>> allConnections = new ArrayList<Connection<?>>();
		if (connections.size() > 0) {
			for (List<Connection<?>> connectionList : connections.values()) {
				for (Connection<?> connection : connectionList) {
					allConnections.add(connection);
				}
			}
		}
		return allConnections;
	}
	
	public void setUsersConnectionRepository(
			UsersConnectionRepository usersConnectionRepository) {
		this.usersConnectionRepository = usersConnectionRepository;
	}


	public void setAuthenticationFactory(
			SpringSocialSecurityAuthenticationFactory authenticationFactory) {
		this.authenticationFactory = authenticationFactory;
	}


	public void setSignUpService(SignUpService signUpService) {
		this.signUpService = signUpService;
	}
		

}
