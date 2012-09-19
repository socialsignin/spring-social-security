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
package org.socialsignin.springsocial.security.signup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.socialsignin.springsocial.security.api.SpringSocialProfile;
import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.socialsignin.springsocial.security.connect.SpringSocialSecurityConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

/**
 * Service which provides local sign-up services when a user
 * creates a local user account using a 3rd party provider
 * for authentication and selects a local username.
 * 
 * This implementation stores local account details such
 * as userName and displayName in the local ConnectionRepository itself 
 * 
 * @author Michael Lavelle
 */
public class ConnectionRepositorySignUpService implements SignUpService {

	@Autowired
	private UsersConnectionRepository usersConnectionRepository;

	@Autowired
	private ConnectionFactoryLocator connectionFactoryLocator;

	private Connection<SpringSocialSecurity> createSpringSocialSecurityConnectionFromProfile(SpringSocialProfile springSocialProfile)
	{
		ConnectionData connectionData = new ConnectionData(
				SpringSocialSecurityConnectionFactory.SPRING_SOCIAL_SECURITY_PROVIDER_NAME,
			springSocialProfile.getUserName(),
			springSocialProfile.getDisplayName(), springSocialProfile.getProfileUrl(),
			springSocialProfile.getImageUrl(), springSocialProfile.getPassword(),
			null, null, null);
		
		
		Connection<SpringSocialSecurity> springSocialSecurityConnection = connectionFactoryLocator
				.getConnectionFactory(SpringSocialSecurity.class)
				.createConnection(connectionData);
			

		return springSocialSecurityConnection;
		
	}
	
	private String generateNewPassword()
	{
		// Passwords must be non-null as:
		// 1) We need to construct User object with non-null password in UserDetailsService
		// 2) We are storing user details in the ConnectionRepository table, with the password
		// being set in the accessToken field which is non-null
		
		// Passwords must be non-empty in order to use remember-me services
		
		return UUID.randomUUID()
		.toString();
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void signUpUser(
			SpringSocialProfile springSocialProfile) throws UsernameAlreadyExistsException{

		// Check if username is available
		if (!isUserIdAvailable(springSocialProfile.getUserName())) {
			throw new UsernameAlreadyExistsException(springSocialProfile.getUserName());
		}
		
		// Populate default password if none is set
		if (springSocialProfile.getPassword() == null) {
			springSocialProfile.setPassword(generateNewPassword());
		}
	
		
		// Create a connection for this spring social security profile	
		Connection<SpringSocialSecurity> springSocialSecurityConnection =
			createSpringSocialSecurityConnectionFromProfile(springSocialProfile);
				

		// Try to persist this connection
		try {
			usersConnectionRepository.createConnectionRepository(
					springSocialProfile.getUserName()).addConnection(
					springSocialSecurityConnection);
		} catch (DuplicateConnectionException e) {
			throw new UsernameAlreadyExistsException(springSocialProfile.getUserName());
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void signUpUserAndCompleteConnection(
			SpringSocialProfile springSocialProfile,
			WebRequest webRequest) throws UsernameAlreadyExistsException {
		signUpUser(springSocialProfile);
		ProviderSignInUtils.handlePostSignUp(springSocialProfile.getUserName(), webRequest);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isUserIdAvailable(String userId) {
		Set<String> providerUserIds = new HashSet<String>();
		providerUserIds.add(userId);
		Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(
				SpringSocialSecurityConnectionFactory.SPRING_SOCIAL_SECURITY_PROVIDER_NAME, providerUserIds);
		return userIds.size() == 0;
	}

	@Override
	@Transactional(readOnly = true)
	public SpringSocialSecurityProfile getUserProfile(String userId) {
		ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(userId);
		List<Connection<SpringSocialSecurity>> connections = connectionRepository.findConnections(SpringSocialSecurity.class);
		if (connections.size() ==1)
		{
			return connections.get(0).getApi().getUserProfile();
		}
		else
		{
			throw new UsernameNotFoundException(userId);
		}
	}
	

}
