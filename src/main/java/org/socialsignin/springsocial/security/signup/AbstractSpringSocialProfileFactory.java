package org.socialsignin.springsocial.security.signup;

import org.socialsignin.springsocial.security.api.SpringSocialProfile;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UserProfile;

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
