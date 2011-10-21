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
package org.socialsignin.springframework.social.security.userauthorities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/**
* Simple implementation of UserAuthoritiesService, by default assigning each user the same default role,
* and a default role for each provider
* @author Michael Lavelle
*/
@Service
public class SimpleUserAuthoritiesService implements UserAuthoritiesService {

	private String defaultAuthorityName = "ROLE_USER";
	
	public String getDefaultProviderAuthorityName(String providerId)
	{
		return defaultAuthorityName + "_" + providerId.toUpperCase();
	}
	
	public void setDefaultAuthorityName(String defaultAuthorityName) {
		this.defaultAuthorityName = defaultAuthorityName;
	}

	protected List<GrantedAuthority> getDefaultAuthorities(Set<String> providerIds)
	{
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority(defaultAuthorityName));
		for (String providerId : providerIds)
		{
			grantedAuthorities.add(getProviderAuthority(providerId));

		}
		return grantedAuthorities;
 
	}
	
	@Override
	public List<GrantedAuthority> getAuthoritiesForUser(Set<String> providerIds,String userId) {
		return getDefaultAuthorities(providerIds);
	}

	@Override
	public GrantedAuthority getProviderAuthority(
			String providerId) {
		return new SimpleGrantedAuthority(getDefaultProviderAuthorityName(providerId));
	}

}
