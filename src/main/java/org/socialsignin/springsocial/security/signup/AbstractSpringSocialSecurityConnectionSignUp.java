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
package org.socialsignin.springsocial.security.signup;

import org.socialsignin.springsocial.security.api.SpringSocialProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

/**
 * An abstract command that signs up a new user in the event no local user id could be
 * mapped from a Connection.
 * 
 * In SpringSocialSecurity, by default there is no need for a local user account
 * to be created outside of spring social - by default just checks and returns
 * user id associated with third party connection.
 * 
 * Returns null if user id already exists locally for a different connection
 * 
 * Default implementation is SpringSocialSecurityConnectionSignUp - alternative implementations can
 * be provided and registered should the implicit SignUp require custom behaviour.
 * 
 * @author Michael Lavelle
 */
public abstract class AbstractSpringSocialSecurityConnectionSignUp<
P extends SpringSocialProfile,S extends SignUpService<P>,F extends AbstractSpringSocialProfileFactory<P>> implements ConnectionSignUp {

	@Autowired
	private S signUpService;
	
	@Autowired
	private F socialProfileFactory;

	public AbstractSpringSocialSecurityConnectionSignUp() {
	}

	public String execute(Connection<?> connection) {
		try
		{
			P springSocialSecurityProfile = 
				socialProfileFactory.create(connection);
			if (springSocialSecurityProfile.getUserName() != null)
			{
				signUpService
				.signUpUser(springSocialSecurityProfile);
			}
			return springSocialSecurityProfile.getUserName();
		}
		catch (UsernameAlreadyExistsException e)
		{
			return null;
		}
	}

	
}
