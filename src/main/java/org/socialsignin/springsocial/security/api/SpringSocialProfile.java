package org.socialsignin.springsocial.security.api;

public interface SpringSocialProfile {
	
	public String getUserName();
	public String getDisplayName();
	public String getPassword();
	public String getImageUrl();
	public String getProfileUrl();
	public void setUserName(String userName);
	public void setDisplayName(String displayName);
	public void setPassword(String password);
	public void setProfileUrl(String profileUrl);
	public void setImageUrl(String imageUrl);

}
