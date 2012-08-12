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
package org.socialsignin.springsocial.security.userauthorities;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.connect.ConnectionKey;

/**
 * A service that provides the granted authorities for a given userId
 * Implemented by {@link SimpleUserAuthoritiesService}.
 * 
 * @author Michael Lavelle
 */
public interface UserAuthoritiesService {

	/**
	 * Obtain the list of authorities for a user given a set of connection keys for that user.
	 * 
	 * @param providerIds
	 * @param userId
	 * @return
	 */
	public List<GrantedAuthority> getAuthoritiesForUser(
			Set<ConnectionKey> providerIds, String userId);

	/**
	 * Obtain a provider-specific authority which is granted to users with connections
	 * to this connectionKey's provider, and optionally include the providerUserId for 
	 * even more fine grained authority scheme - allowing authorities to be granted on the
	 * basis of a specific connection to a provider.
	 * 
	 * @param providerId
	 * @return A general provider-specific authority which is granted to users with connections
	 * to this provider
	 */
	public GrantedAuthority getProviderAuthority(ConnectionKey connectionKey);
	
	/**
	 * Obtain a general provider-specific authority which is granted to users with connections
	 * to this provider
	 * 
	 * @param providerId
	 * @return A general provider-specific authority which is granted to users with connections
	 * to this provider
	 */
	public GrantedAuthority getProviderAuthority(String providerId);


}
