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

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.api.impl.SpringSocialSecurityTemplate;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

/**
 * SpringSocialSecurity ServiceProvider implementation

 * 
 * @author Michael Lavelle
 */
public class SpringSocialSecurityServiceProvider extends AbstractOAuth2ServiceProvider<SpringSocialSecurity>
{

	public SpringSocialSecurityServiceProvider() {
		super(null);
	}
	

	public SpringSocialSecurity getSpringSocialSecurity(
			ConnectionData connectionData) {
		return new SpringSocialSecurityTemplate(connectionData);
	}

	@Override
	public SpringSocialSecurity getApi(String accessToken) {
		// API is not retrieved using access token - use getSpringSocialSecurity() method instead
		return null;
	}

}
