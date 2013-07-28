/*
 * Copyright 2013 the original author or authors.
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
package org.socialsignin.springsocial.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.connect.SpringSocialSecurityConnectionFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.provider.AbstractSocialAuthenticationService;
import org.springframework.social.security.provider.OAuth2AuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.util.Assert;

/**
 * 
* @author Michael Lavelle
 *
 */
public class SpringSocialSecurityAuthenticationService extends AbstractSocialAuthenticationService<SpringSocialSecurity> {

	
	private ConnectionFactory<SpringSocialSecurity> connectionFactory;

	public SpringSocialSecurityAuthenticationService() {
		setConnectionFactory(new SpringSocialSecurityConnectionFactory());
	}

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(getConnectionFactory(), "connectionFactory");
		}
	
	@Override
	public ConnectionFactory<SpringSocialSecurity> getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(
			ConnectionFactory<SpringSocialSecurity> connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public SocialAuthenticationToken getAuthToken(HttpServletRequest request,
			HttpServletResponse response)
			throws SocialAuthenticationRedirectException {
		return null;
	}

	
}
