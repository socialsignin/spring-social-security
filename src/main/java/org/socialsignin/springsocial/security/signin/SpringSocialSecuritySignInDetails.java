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
package org.socialsignin.springsocial.security.signin;

import org.springframework.social.connect.Connection;

/**
* POJO for holding userid and connection, set in session by SpringSocialSecuritySignInService
* and retrieved from session in SpringSocialSecurityAuthenticationFilter
* 
* @author Michael Lavelle
*/
public class SpringSocialSecuritySignInDetails {

	public String getUserId() {
		return userId;
	}

	public Connection<?> getConnection() {
		return connection;
	}

	private String userId;
	private Connection<?> connection;
	
	public SpringSocialSecuritySignInDetails(String userId,Connection<?> connection)
	{
		this.userId = userId;
		this.connection = connection;
	}
	
}
