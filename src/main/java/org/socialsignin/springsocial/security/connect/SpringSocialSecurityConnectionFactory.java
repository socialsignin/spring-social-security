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

import javax.annotation.PostConstruct;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.connect.support.SpringSocialSecurityConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.stereotype.Service;

/**
 * SpringSocialSecurity ConnectionFactory implementation
 * 
 * @author Michael Lavelle
 */
@Service
public class SpringSocialSecurityConnectionFactory extends
		ConnectionFactory<SpringSocialSecurity> {

	public final static String SPRING_SOCIAL_SECURITY_PROVIDER_NAME = "springSocialSecurity";
	
	@Autowired
	private ConnectionFactoryRegistry connectionFactoryRegistry;

	public SpringSocialSecurityConnectionFactory() {
		super(SPRING_SOCIAL_SECURITY_PROVIDER_NAME,
				new SpringSocialSecurityServiceProvider(),
				new SpringSocialSecurityAdapter());
	}

	@PostConstruct
	public void registerWithConnectionFactoryRegistry() {
		connectionFactoryRegistry.addConnectionFactory(this);
	}

	@Override
	public Connection<SpringSocialSecurity> createConnection(ConnectionData data) {
		return new SpringSocialSecurityConnection(
				(SpringSocialSecurityServiceProvider) getServiceProvider(),
				data, getApiAdapter());

	}

}
