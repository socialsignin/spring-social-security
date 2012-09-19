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
package org.socialsignin.springsocial.security.api.impl;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.springframework.social.connect.ConnectionData;

/**
 * Implementation of <code>SpringSocialSecurity</code> whose internal representation
 * of local user profile data is a ConnectionData instance.  Allows a <code>ConnectionRepository</code>
 * to be used as a persistence mechanism for <code>SpringSocalSecurityProfile</code>s
 * 
 * @author Michael Lavelle
 */
public class SpringSocialSecurityTemplate implements SpringSocialSecurity {

	private ConnectionData connectionData;

	public SpringSocialSecurityTemplate(ConnectionData connectionData) {
		this.connectionData = connectionData;
	}

	/**
	 * Obtain the local user's account details from wrapped
	 * <code>ConnectionData</code> instance.
	 */
	@Override
	public SpringSocialSecurityProfile getUserProfile() {

		return new SpringSocialSecurityProfile(
				connectionData.getProviderUserId(),
				connectionData.getDisplayName(),
				connectionData.getAccessToken(),
				connectionData.getImageUrl(),
				connectionData.getProfileUrl());
	}

}
