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
package org.socialsignin.springsocial.security.api;

/**
 * Represents a local user profile in a spring-social-security application for the typical use-case  
 * where users authenticate using 3rd party providers, simply selecting a local userName and optional displayName on registration
 * (which can be defaulted to those provided by the 3rd party if appropriate). 
 * 
 * In spring-social security applications a user does not need to choose a local password as part of their sign up process as
 * authentication is delegated to 3rd party, however a non-empty local password *is* required if remember-me services are
 * to be used. Implementations of <code>SignUpService</code> can choose whether to give the user an option to pick a password
 * or could auto-generate a password on behalf of the user (the default behavior of <code>ConnectionRepositorySignUpService</code> ).
 * 
 * The imageUrl and profileUrl attributes support the common scenario where spring-social applications create web-accessible
 * profiles on behalf of their users which pull in data from 3rd party providers, but may wish to give their users the
 * opportunity to customise their profile url and image. 
 * 
 * @author Michael Lavelle
 */
public class SpringSocialSecurityProfile implements SpringSocialProfile {

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
