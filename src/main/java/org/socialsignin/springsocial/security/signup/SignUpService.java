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

import org.socialsignin.springsocial.security.api.SpringSocialProfile;
import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.springframework.web.context.request.WebRequest;

/**
 * Service which provides local sign-up services when a user
 * creates a local user account using a 3rd party provider
 * for authentication and selects a local username.
 * 
 * @author Michael Lavelle
 */
public interface SignUpService<P extends SpringSocialProfile> {

	/**
	 * Creates a local user account only but does not complete connection
	 * with spring-social
	 *
	 * Called by SpringSocialSecurityConnectionSignUp when local
	 * user accounts are created implicitly on connection with
	 * a 3rd party provider.
	 *
	 * @param springSocialProfile
	 * @param webRequest
	 * @throws UsernameAlreadyExistsException
	 */
	public void signUpUser(
			P springSocialProfile) throws UsernameAlreadyExistsException;

	/**
	 * Creates a local user account and completes the connection
	 * to spring-social in a single unit-of-work, allowing
	 * implementations to ensure that these steps behave transactionally
	 *
	 * Called by SpringSocialSecuritySignUpController when user submits
	 * their signup form
	 *
	 * @param springSocialProfile
	 * @param webRequest
	 * @throws UsernameAlreadyExistsException
	 */
	public void signUpUserAndCompleteConnection(
			P springSocialProfile,
			WebRequest webRequest) throws UsernameAlreadyExistsException;

	/**
	 * Checks if a local user id is available
	 * 
	 * @param userId The userid to check for availablity
	 * @return
	 */
	public boolean isUserIdAvailable(String userId);
	
	/**
	 * Obtain the local user's profile
	 * 
	 * @param userId The userId of the user whose profile we wish to retrieve
	 * @return
	 */
	public P getUserProfile(String userId);

}
