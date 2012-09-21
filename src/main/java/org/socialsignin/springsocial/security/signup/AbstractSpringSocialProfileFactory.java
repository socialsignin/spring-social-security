package org.socialsignin.springsocial.security.signup;
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
import org.socialsignin.springsocial.security.api.SpringSocialProfile;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UserProfile;

/**
 * Abstract factory for creating and populating local user profiles, using a 
 * third party connection to set default attributes.
 * 
 * Default implementation is <code>SpringSocialSecurityProfileFactory</code> which creates
 * minimal SpringSocialSecurityProfile instances - alternative implementations can be
 * registered if more involved local account details are required.
 * 
 * @author Michael Lavelle
 */
public abstract class AbstractSpringSocialProfileFactory<P extends SpringSocialProfile> {

	public P create(Connection<?> connection)
	{
		P profile = instantiate();
		UserProfile userProfile = connection.fetchUserProfile();
		init(profile,userProfile,connection.createData());
		return profile;
		
	}
	public void init(P profile,UserProfile userProfile,ConnectionData connectionData)
	{
		if (userProfile != null)
		{
			profile.setUserName(userProfile.getUsername());
			profile.setDisplayName(userProfile.getName());
		}
	}
	public abstract P instantiate();

}
