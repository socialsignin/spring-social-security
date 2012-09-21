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

import java.util.Collection;

import org.socialsignin.springsocial.security.userauthorities.UserAuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Subclasses of this class, registered as ConnectInterceptors
 * in spring social, 
 * 
 * @author Michael Lavelle
 */
public class SpringSocialSecurityConnectInterceptor<S> extends
		EnsureUniqueConnectInterceptor<S> implements ConnectInterceptor<S> {

	@Autowired
	@Qualifier("userAuthoritiesService")
	private UserAuthoritiesService userAuthoritiesService;

	@Autowired(required = false)
	private RememberMeServices rememberMeServices;
	
	private final static String SAVED_REQUEST_URL_ATTRIBUTE_NAME = "springSocialSecurityConnectInterceptorSavedRequestUrl";

	private RequestCache requestCache = new HttpSessionRequestCache();

	@Autowired
	private SpringSocialSecurityAuthenticationFactory authenticationFactory;

	/**
	 * This callback 1)  Ensures that 2 different local users
	 * cannot share the same 3rd party connection 2) Updates the current
	 * user's authentication if the set of roles they are assigned
	 * needs to change now that this connection has been made.
	 * 3) Looks for a request previously saved by an access denied
	 * handler, and if present, sets the url of this original
	 * pre-authorisation request as a session attribute
	 * 
	 */
	@Override
	public void postConnect(Connection<S> connection, WebRequest webRequest) {

		super.postConnect(connection, webRequest);

		/**
		 * User roles are generated according to connected
		 * providers in spring-social-security
		 * 
		 * Now that this connection has been made,
		 * doe we need to update the user roles?
		 * 
		 * If so, update the current user's authentication and update
		 * remember-me services accordingly.
		 */
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		
		Collection<? extends GrantedAuthority> existingAuthorities = authentication
				.getAuthorities();
		
		GrantedAuthority newAuthority = userAuthoritiesService
				.getProviderAuthority(connection.getKey());
		
		if (!existingAuthorities.contains(newAuthority)) {
			
			Authentication newAuthentication = authenticationFactory
					.updateAuthenticationForNewConnection(authentication,
							connection);
			SecurityContextHolder.getContext().setAuthentication(
					newAuthentication);
			
			if (rememberMeServices != null
					&& webRequest instanceof ServletWebRequest) {
				
				ServletWebRequest servletWebRequest = ((ServletWebRequest) webRequest);
				rememberMeServices.loginSuccess(servletWebRequest.getRequest(),
						servletWebRequest.getResponse(), newAuthentication);
			}
		}
		
		/**
		 * This connection may have been instigated by an 
		 * access denied handler which may have saved the
		 * original request made by the user before their access
		 * was denied.  
		 * 
		 * Spring Social sends the user to a particular view
		 * on completion of connection.  We may wish to offer the
		 * user a "continue" link on this view, allowing their
		 * original request (if saved by the access denied handler)
		 * to be re-attempted
		 *
		 */
		if (webRequest instanceof ServletWebRequest)
		{
			ServletWebRequest servletWebRequest
			= (ServletWebRequest)webRequest;
			SavedRequest savedRequest = requestCache.getRequest(servletWebRequest.getRequest(), servletWebRequest.getResponse());
			if (savedRequest != null)
			{
				String redirectUrl = savedRequest.getRedirectUrl();
				if (redirectUrl != null && savedRequest.getMethod().equalsIgnoreCase("get"))
				{
					servletWebRequest.setAttribute(SAVED_REQUEST_URL_ATTRIBUTE_NAME, savedRequest.getRedirectUrl(), RequestAttributes.SCOPE_SESSION);
				}
			}
		}
	}

	

	/**
	 * No Op
	 */
	@Override
	public void preConnect(ConnectionFactory<S> arg0,
			MultiValueMap<String, String> arg1, WebRequest arg2) {
			// No-op
	}
	

	/**
	 * Set a request cache here to change the default 
	 * <code>HttpSessionRequestCache</code> used by this class
	 * to determine if a saved request was set previously
	 * by an access denied handler.
	 * 
	 * @param requestCache
	 */
	public void setRequestCache(RequestCache requestCache) {
		this.requestCache = requestCache;
	}
	
	/**
	 * @param rememberMeServices Optional remember-me services, required if we wish remember-me services
	 * to be notified when a user-authorisation change occurs as a result of this connection
	 */
	public void setRememberMeServices(RememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}

}
