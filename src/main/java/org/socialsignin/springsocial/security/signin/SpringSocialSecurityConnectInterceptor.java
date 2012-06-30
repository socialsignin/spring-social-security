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
package org.socialsignin.springsocial.security.signin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.socialsignin.springsocial.security.userauthorities.UserAuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
* @author Michael Lavelle
*/
public class SpringSocialSecurityConnectInterceptor<S> extends EnsureUniqueConnectInterceptor<S> implements
		ConnectInterceptor<S> {
	
	@Autowired
	@Qualifier("userAuthoritiesService")
	private UserAuthoritiesService userAuthoritiesService;
	
	@Autowired(required=false)
	private RememberMeServices rememberMeServices;

	@Override
	public void postConnect(Connection<S> connection, WebRequest webRequest) {
		
		super.postConnect(connection, webRequest);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> existingAuthorities = authentication.getAuthorities();
		GrantedAuthority newAuthority = userAuthoritiesService.getProviderAuthority(connection.getKey());
		
		if (!existingAuthorities.contains(newAuthority))
		{
			Set<String> providerIds =new HashSet<String>();
			providerIds.add(connection.getKey().getProviderId());
			List<GrantedAuthority> newAuthorities = new ArrayList<GrantedAuthority>();
			newAuthorities.add(newAuthority);
			newAuthorities.addAll(existingAuthorities);
			Authentication newAuthentication = new UsernamePasswordAuthenticationToken(authentication.getName(), new SpringSocialSecurityPasswordBuilder((String)authentication.getCredentials(),connection.createData()).build(),newAuthorities);		
			SecurityContextHolder.getContext().setAuthentication(newAuthentication);
			if (rememberMeServices != null && webRequest instanceof ServletWebRequest)
			{
				ServletWebRequest servletWebRequest = ((ServletWebRequest)webRequest);
				rememberMeServices.loginSuccess(servletWebRequest.getRequest(), servletWebRequest.getResponse(), newAuthentication);
			}
		}
	} 

	@Override
	public void preConnect(ConnectionFactory<S> arg0,
			MultiValueMap<String, String> arg1, WebRequest arg2) {
		
	}

}
