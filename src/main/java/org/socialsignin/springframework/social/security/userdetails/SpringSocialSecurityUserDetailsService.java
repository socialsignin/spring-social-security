package org.socialsignin.springframework.social.security.userdetails;

import java.util.ArrayList;
import java.util.List;

import org.socialsignin.springframework.social.security.userauthorities.UserAuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
UserDetailsService for SpringSocialSecurity - retrieves user details from Spring Social
connection repository

* @author Michael Lavelle
*/
@Repository
@Service
@Transactional(readOnly = true)
@Qualifier("springSocialSecurityUserDetailsService")
public class SpringSocialSecurityUserDetailsService implements UserDetailsService {

	@Autowired
	private UsersConnectionRepository usersConnectionRepository;
	
	@Autowired
	private UserAuthoritiesService userAuthoritiesService;
	
	
	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(userName);
		MultiValueMap<String,Connection<?>> connections = connectionRepository.findAllConnections();
		
		List<Connection<?>> allConnections = new ArrayList<Connection<?>>();
		if (connections.size() > 0)
		{
			for (List<Connection<?>> connectionList : connections.values())
			{
				for (Connection<?> connection : connectionList)
				{
					allConnections.add(connection);
				}
			}
			if (allConnections.size() > 0)
			{
				return new User(userName,null,true,true,true,true,userAuthoritiesService.getAuthoritiesForUser(userName));
			}
			else
			{
				throw new UsernameNotFoundException(userName);
				
			}	
		}
		else
		{
			throw new UsernameNotFoundException(userName);
		}
		
		
	
		
		
		
	}

}
