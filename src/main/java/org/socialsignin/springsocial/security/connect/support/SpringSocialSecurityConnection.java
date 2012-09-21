package org.socialsignin.springsocial.security.connect.support;

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
import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.connect.SpringSocialSecurityServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.AbstractConnection;

/**
 * Represents a Connection to spring-social-security : allows
 * services provided by spring-social-security (such as retrieving
 * a local user's profile) to be accessed in the same way as 
 * external 3rd party are accessed and means that a ConnectionRepository
 * can be used to persist local user accounts.
 * 
 * @author Michael Lavelle
 */
public class SpringSocialSecurityConnection extends
		AbstractConnection<SpringSocialSecurity> {

	private SpringSocialSecurityServiceProvider serviceProvider;
	private String password;

	public SpringSocialSecurityConnection(
			SpringSocialSecurityServiceProvider serviceProvider,
			ApiAdapter<SpringSocialSecurity> apiAdapter) {
		super(apiAdapter);
		this.serviceProvider = serviceProvider;
	}

	public SpringSocialSecurityConnection(
			SpringSocialSecurityServiceProvider serviceProvider,
			ConnectionData data, ApiAdapter<SpringSocialSecurity> apiAdapter) {
		super(data, apiAdapter);
		this.serviceProvider = serviceProvider;
		this.password = data.getAccessToken();
	}

	@Override
	public SpringSocialSecurity getApi() {
		synchronized (getMonitor()) {
			return serviceProvider.getSpringSocialSecurity(createData());
		}
	}

	public ConnectionData createData() {
		synchronized (getMonitor()) {
			return new ConnectionData(getKey().getProviderId(), getKey()
					.getProviderUserId(), getDisplayName(), getProfileUrl(),
					getImageUrl(), password, null, null, null);
		}
	}

}
