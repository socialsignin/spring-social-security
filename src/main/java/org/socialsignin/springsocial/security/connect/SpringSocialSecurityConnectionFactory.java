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
package org.socialsignin.springsocial.security.connect;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.connect.support.SpringSocialSecurityOAuth2Connection;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;

/**
 * SpringSocialSecurity ConnectionFactory implementation
 * 
 * @author Michael Lavelle
 */
public class SpringSocialSecurityConnectionFactory extends
		OAuth2ConnectionFactory<SpringSocialSecurity> {

	public final static String SPRING_SOCIAL_SECURITY_PROVIDER_NAME = "springSocialSecurity";

	
	public SpringSocialSecurityConnectionFactory() {
		super(SPRING_SOCIAL_SECURITY_PROVIDER_NAME, new SpringSocialSecurityServiceProvider(), new SpringSocialSecurityAdapter());
	}


	@Override
	public Connection<SpringSocialSecurity> createConnection(ConnectionData data) {
		return new SpringSocialSecurityOAuth2Connection(data,(SpringSocialSecurityServiceProvider) getServiceProvider(),getApiAdapter());
	}
}
