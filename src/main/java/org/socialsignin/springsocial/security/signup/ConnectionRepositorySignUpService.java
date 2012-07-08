package org.socialsignin.springsocial.security.signup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

@Service
public class ConnectionRepositorySignUpService implements SignUpService {

	@Autowired
	private UsersConnectionRepository usersConnectionRepository;

	@Autowired
	private ConnectionFactoryLocator connectionFactoryLocator;

	private Connection<SpringSocialSecurity> createSpringSocialSecurityConnectionFromProfile(SpringSocialSecurityProfile springSocialSecurityProfile)
	{
		ConnectionData connectionData = new ConnectionData(
				SpringSocialSecurityConnectionFactory.SPRING_SOCIAL_SECURITY_PROVIDER_NAME,
			springSocialSecurityProfile.getUserName(),
			springSocialSecurityProfile.getPassword(), springSocialSecurityProfile.getProfileUrl(),
			springSocialSecurityProfile.getImageUrl(), springSocialSecurityProfile.getPassword(),
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
			SpringSocialSecurityProfile springSocialSecurityProfile) throws UsernameAlreadyExistsException{

		// Check if username is available
		if (!isUserIdAvailable(springSocialSecurityProfile.getUserName())) {
			throw new UsernameAlreadyExistsException(springSocialSecurityProfile.getUserName());
		}
		
		// Populate default password if none is set
		if (springSocialSecurityProfile.getPassword() == null) {
			springSocialSecurityProfile.setPassword(generateNewPassword());
		}
	
		
		// Create a connection for this spring social security profile	
		Connection<SpringSocialSecurity> springSocialSecurityConnection =
			createSpringSocialSecurityConnectionFromProfile(springSocialSecurityProfile);
				

		// Try to persist this connection
		try {
			usersConnectionRepository.createConnectionRepository(
					springSocialSecurityProfile.getUserName()).addConnection(
					springSocialSecurityConnection);
		} catch (DuplicateConnectionException e) {
			throw new UsernameAlreadyExistsException(springSocialSecurityProfile.getUserName());
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void signUpUserAndCompleteConnection(
			SpringSocialSecurityProfile springSocialSecurityProfile,
			WebRequest webRequest) throws UsernameAlreadyExistsException {
		signUpUser(springSocialSecurityProfile);
		ProviderSignInUtils.handlePostSignUp(springSocialSecurityProfile.getUserName(), webRequest);
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
