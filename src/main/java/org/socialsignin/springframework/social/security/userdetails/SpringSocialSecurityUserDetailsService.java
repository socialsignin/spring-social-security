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
package org.socialsignin.springframework.social.security.userdetails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.socialsignin.springframework.social.security.userauthorities.UserAuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
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
		Set<ConnectionKey> allConnectionKeys = new HashSet<ConnectionKey>();
		List<Connection<?>> allConnections = new ArrayList<Connection<?>>();
		if (connections.size() > 0)
		{
			for (List<Connection<?>> connectionList : connections.values())
			{
				for (Connection<?> connection : connectionList)
				{
					allConnections.add(connection);
					if(!allConnectionKeys.contains(connection.getKey()))
					{
						allConnectionKeys.add(connection.getKey());
					}
				}
			}
			if (allConnections.size() > 0)
			{
				return new User(userName,"",true,true,true,true,userAuthoritiesService.getAuthoritiesForUser(allConnectionKeys,userName));
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
