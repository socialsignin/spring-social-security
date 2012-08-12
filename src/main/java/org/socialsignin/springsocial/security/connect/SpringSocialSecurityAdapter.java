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
package org.socialsignin.springsocial.security.connect;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

/**
 * SpringSocialSecurity ApiAdapter implementation
 * 
 * @author Michael Lavelle
 */
public class SpringSocialSecurityAdapter implements
		ApiAdapter<SpringSocialSecurity> {

	@Override
	public boolean test(SpringSocialSecurity springSocialSecurity) {
		return true;
	}

	@Override
	public void setConnectionValues(SpringSocialSecurity springSocialSecurity,
			ConnectionValues values) {

		SpringSocialSecurityProfile profile = springSocialSecurity
				.getUserProfile();
		values.setProviderUserId(profile.getUserName());
		values.setDisplayName(profile.getDisplayName());

	}

	@Override
	public UserProfile fetchUserProfile(
			SpringSocialSecurity springSocialSecurity) {
		SpringSocialSecurityProfile profile = springSocialSecurity
				.getUserProfile();
		UserProfileBuilder builder = new UserProfileBuilder();
		builder.setUsername(profile.getUserName());
		builder.setName(profile.getDisplayName());
		return builder.build();
	}

	@Override
	public void updateStatus(SpringSocialSecurity connectionRepository,
			String message) {

	}

}
