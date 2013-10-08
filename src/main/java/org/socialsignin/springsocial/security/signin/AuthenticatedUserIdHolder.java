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
package org.socialsignin.springsocial.security.signin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Convenience helper class for obtaining a user id if available from 
 * security context.
 * 
 * Deprecated - use UserIdSource instead
 * 
 * @author Michael Lavelle
 */
@Deprecated
public class AuthenticatedUserIdHolder {

	/**
	 * @return The userId of the currently authenticated user
	 * or null if the local user is anonymous
	 */
	public static String getAuthenticatedUserId() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		return auth == null || auth.getName().equals("anonymousUser") ? null
				: auth.getName();
	}

}
