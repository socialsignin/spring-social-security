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

import java.util.UUID;

import org.socialsignin.springsocial.security.api.SpringSocialProfile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

/**
 * Abstract SignUpService implementation which provides local sign-up services when a user
 * creates a local user account using a 3rd party provider
 * for authentication and selects a local username.
 * 
 * Default implementation is ConnectionRepositorySignUpService which stores minimal
 * local use account details inside existing Spring Social Connection Repository.
 * 
 * Alternative implementations can be registered instead to support collecting custom
 * sign up details and/or storing to an alternative persistent store
 * 
 * @author Michael Lavelle
 */
@Service
public abstract class AbstractSignUpService<P extends SpringSocialProfile> implements SignUpService<P> {

	protected String generateNewPassword()
	{
		// Passwords must be non-null as:
		// 1) We need to construct User object with non-null password in UserDetailsService
		// 2) We are storing user details in the ConnectionRepository table, with the password
		// being set in the accessToken field which is non-null
		
		// Passwords must be non-empty in order to use remember-me services
		
		return UUID.randomUUID()
		.toString();
	}
	
	protected abstract void save(P springSocialProfile) throws UsernameAlreadyExistsException;
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void signUpUser(
			P springSocialProfile) throws UsernameAlreadyExistsException{

		// Check if username is available
		if (!isUserIdAvailable(springSocialProfile.getUserName())) {
			throw new UsernameAlreadyExistsException(springSocialProfile.getUserName());
		}
		
		// Populate default password if none is set
		if (springSocialProfile.getPassword() == null) {
			springSocialProfile.setPassword(generateNewPassword());
		}
	
		
		save(springSocialProfile);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void signUpUserAndCompleteConnection(
			P springSocialProfile,
			WebRequest webRequest) throws UsernameAlreadyExistsException {
		signUpUser(springSocialProfile);
		ProviderSignInUtils.handlePostSignUp(springSocialProfile.getUserName(), webRequest);
	}

	@Override
	@Transactional(readOnly = true)
	public abstract boolean isUserIdAvailable(String userId);


	@Override
	@Transactional(readOnly = true)
	public abstract P getUserProfile(String userId) throws UsernameNotFoundException;

	

}
