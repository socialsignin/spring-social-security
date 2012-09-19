package org.socialsignin.springsocial.security.signup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.socialsignin.springsocial.security.api.SpringSocialProfile;
import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.connect.SpringSocialSecurityConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Service;


/**
 * Abstract SignUpService implementation which provides local sign-up services when a user
 * creates a local user account using a 3rd party provider
 * for authentication and selects a local username.
 * 
 * This implementation stores local account details such
 * as userName and displayName in the local ConnectionRepository itself
 * 
 * @author Michael Lavelle
 */
@Service
public class ConnectionRepositorySignUpService extends AbstractSignUpService<SpringSocialProfile> {

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
	
	
	@Override
	protected void save(SpringSocialProfile springSocialProfile)
			throws UsernameAlreadyExistsException {

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
	public boolean isUserIdAvailable(String userId) {
		Set<String> providerUserIds = new HashSet<String>();
		providerUserIds.add(userId);
		Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(
				SpringSocialSecurityConnectionFactory.SPRING_SOCIAL_SECURITY_PROVIDER_NAME, providerUserIds);
		return userIds.size() == 0;
	}

	@Override
	public SpringSocialProfile getUserProfile(String userId)
			throws UsernameNotFoundException {
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
