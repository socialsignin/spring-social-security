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
package org.socialsignin.springsocial.security.signup;

import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;

/**
 * A command that signs up a new user in the event no local user id could be
 * mapped from a Connection.
 * 
 * In SpringSocialSecurity, by default there is no need for a local user account
 * to be created outside of spring social - by default just checks and returns
 * user id associated with third party connection.
 * 
 * Returns null if user id already exists locally for a different connection
 * 
 * @author Michael Lavelle
 */
@Service
public class SpringSocialSecurityConnectionSignUp implements ConnectionSignUp {

	@Autowired
	private SignUpService signUpService;

	public SpringSocialSecurityConnectionSignUp() {
	}

	public String execute(Connection<?> connection) {
		UserProfile userProfile = connection.fetchUserProfile();
		try
		{
			SpringSocialSecurityProfile springSocialSecurityProfile = 
				generateSpringSocialSecurityProfile(userProfile);
			signUpService
				.signUpUser(generateSpringSocialSecurityProfile(userProfile));
			return springSocialSecurityProfile.getUserName();
		}
		catch (UsernameAlreadyExistsException e)
		{
			return null;
		}
	}

	public SpringSocialSecurityProfile generateSpringSocialSecurityProfile(
			UserProfile userProfile) {
		SpringSocialSecurityProfile springSocialSecurityProfile = new SpringSocialSecurityProfile(
				userProfile.getUsername());
		return springSocialSecurityProfile;
	}

}
