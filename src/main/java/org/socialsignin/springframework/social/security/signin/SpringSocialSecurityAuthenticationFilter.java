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
package org.socialsignin.springframework.social.security.signin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.socialsignin.springframework.social.security.signin.AuthenticatedUserIdHolder;
import org.socialsignin.springframework.social.security.signin.SpringSocialSecuritySignInDetails;
import org.socialsignin.springframework.social.security.signin.SpringSocialSecuritySignInService;
import org.socialsignin.springframework.social.security.userauthorities.UserAuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Component;

/**
* Processes an SpringSocialSecurity authentication request.
* Checks for session attribute containing userid and connection as set by SpringSocialSecuritySignInService
* and returns a UsernamePasswordAuthenticationToken

  This filter by default responds to the URL /authenticate. 
* @author Michael Lavelle
*/
@Component
@Qualifier("springSocialSecurityAuthenticationFilter")
public class SpringSocialSecurityAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private boolean removeSignInDetailsFromSessionOnSuccessfulAuthentication = true;
	
	private boolean allowRepeatedAuthenticationAttempts = false;
	
	public void setAllowRepeatedAuthenticationAttempts(
			boolean allowRepeatedAuthenticationAttempts) {
		this.allowRepeatedAuthenticationAttempts = allowRepeatedAuthenticationAttempts;
	}
 
	@Override
	@Autowired(required=false)
	public void setRememberMeServices(RememberMeServices rememberMeServices) {
		super.setRememberMeServices(rememberMeServices);
	}

	@Autowired
	@Qualifier("userAuthoritiesService")
	private UserAuthoritiesService userAuthoritiesService;
	
	@Autowired
	@Qualifier("springSocialSecurityUserDetailsService")
	private UserDetailsService userDetailsService;
	
	
	public void setRemoveSignInDetailsFromSessionOnSuccessfulAuthentication(
			boolean removeSignInDetailsFromSessionOnSuccessfulAuthentication) {
		this.removeSignInDetailsFromSessionOnSuccessfulAuthentication = removeSignInDetailsFromSessionOnSuccessfulAuthentication;
	}

	@Autowired
	public void setAuthenticationManager(AuthenticationManager authenticationManager)
	{
		super.setAuthenticationManager(authenticationManager);
	}

	protected SpringSocialSecurityAuthenticationFilter() {
		super("/authenticate");
	}
	
	protected SpringSocialSecurityAuthenticationFilter(String authenticationUrl) {
		super(authenticationUrl);
	}
	
	

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		
		SpringSocialSecuritySignInDetails signInDetails = (SpringSocialSecuritySignInDetails)request.getSession().getAttribute(SpringSocialSecuritySignInService.SIGN_IN_DETAILS_SESSION_ATTRIBUTE_NAME);
		String alreadyAuthenticatedUserId = AuthenticatedUserIdHolder.getAuthenticatedUserId();

		if (signInDetails != null)
		{		
			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			UserDetails user = userDetailsService.loadUserByUsername(signInDetails.getUserId());
			authorities.addAll(user.getAuthorities());
			GrantedAuthority newAuthority = userAuthoritiesService.getProviderAuthority(signInDetails.getConnection().getKey());
			if (!authorities.contains(newAuthority))
			{
				authorities.add(newAuthority);		
			}
			if (removeSignInDetailsFromSessionOnSuccessfulAuthentication)
			{
				request.getSession().removeAttribute(SpringSocialSecuritySignInService.SIGN_IN_DETAILS_SESSION_ATTRIBUTE_NAME);
			}
			return new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword(),authorities);
		}
		else if (allowRepeatedAuthenticationAttempts && alreadyAuthenticatedUserId != null)
		{
			return SecurityContextHolder.getContext().getAuthentication();
		}
		else
		{
			throw new InsufficientAuthenticationException("SpringSocialSecurity sign in details not found in session");
		}
		
		
	}

}
