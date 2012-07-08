package org.socialsignin.springsocial.security.connect;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

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
