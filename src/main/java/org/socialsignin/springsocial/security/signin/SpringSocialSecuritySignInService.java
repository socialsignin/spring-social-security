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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Adapter that bridges between a ProviderSignInController and a
 * SpringSocialSecuritySignInService. Invoked at the end of a provider sign-in
 * attempt to sign-in the local user account associated with the provider user
 * account.
 * 
 * Places sign in details in the session so that they can be accessed by
 * SpringSocialSecurityAuthenticationFilter
 * 
 * @author Michael Lavelle
 */
@Service
public class SpringSocialSecuritySignInService implements SignInAdapter {

	public final static String SIGN_IN_DETAILS_SESSION_ATTRIBUTE_NAME = "org.socialsignin.springsocial.security.signInDetails";

	
	@Value("${socialsignin.useSocialAuthenticationFilter:false}")
	private boolean useSocialAuthenticationFilter;

	@Autowired
	private SpringSocialSecurityAuthenticationFactory authenticationFactory;
	
	@Autowired(required=false)
	private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

	@Autowired
	@Qualifier("springSocialSecurityUserDetailsService")
	private UserDetailsService userDetailsService;
	
	
	public String signIn(String localUserId, Connection<?> connection,
			NativeWebRequest nativeWebRequest) {

		
		if (useSocialAuthenticationFilter)
		{
			UserDetails userDetails = userDetailsService.loadUserByUsername(localUserId);
			Authentication authentication = authenticationFactory.createAuthenticationFromUserDetails(userDetails);
			SecurityContextHolder.getContext().setAuthentication(authentication);	
			return localUserId;
		}
		else
		{
			sessionStrategy.setAttribute(nativeWebRequest,SIGN_IN_DETAILS_SESSION_ATTRIBUTE_NAME, new SpringSocialSecuritySignInDetails(localUserId, connection.createData()));
			return null;
		}
	}
	
	public void setSessionStrategy(SessionStrategy sessionStrategy) {
		this.sessionStrategy = sessionStrategy;
	}

}