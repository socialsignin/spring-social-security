package org.socialsignin.springsocial.security.api;

public class SpringSocialSecurityProfile {

	private String userName;
	private String displayName;
	private String password;
	private String imageUrl;
	private String profileUrl;
	
	public SpringSocialSecurityProfile() {
	}

	public SpringSocialSecurityProfile(String userName) {
		this.userName = userName;
	}
	public SpringSocialSecurityProfile(String userName, String displayName) {
		this.userName = userName;
		this.displayName = displayName;
	}
	public SpringSocialSecurityProfile(String userName, String displayName,
			String password,String imageUrl,String profileUrl) {
		this.userName = userName;
		this.displayName = displayName;
		this.password = password;
		this.imageUrl = imageUrl;
		this.profileUrl = profileUrl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}


}
