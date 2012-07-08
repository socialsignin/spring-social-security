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

import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.socialsignin.springsocial.security.signin.SpringSocialSecurityAuthenticationFilter;
import org.socialsignin.springsocial.security.signin.SpringSocialSecuritySignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * Controller for displaying/procesing a sign up form and handling chosen user
 * id, checking if it already exists
 * 
 * @author Michael Lavelle
 */
@Controller
@RequestMapping("/signup")
public class SpringSocialSecuritySignUpController {

	@Value("${socialsignin.signUpView}")
	private String signUpView;
	
	private String authenticateUrl = SpringSocialSecurityAuthenticationFilter.DEFAULT_AUTHENTICATION_URL;

	public void setAuthenticateUrl(String authenticateUrl) {
		this.authenticateUrl = authenticateUrl;
	}

	@Autowired
	private SpringSocialSecuritySignInService springSocialSecuritySignInService;

	@Autowired
	private SignUpService signUpService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String signUpForm(
			@ModelAttribute("signUpForm") SpringSocialSecurityProfile signUpForm) {
		return signUpView;
	}

	@ModelAttribute("signUpForm")
	public SpringSocialSecurityProfile createForm(ServletWebRequest request) {
		Connection<?> connection = ProviderSignInUtils.getConnection(request);
		SpringSocialSecurityProfile signUpForm = new SpringSocialSecurityProfile();
		if (connection != null) {
			String thirdPartyUserName = connection.fetchUserProfile()
					.getUsername();
			if (thirdPartyUserName != null
					&& signUpService.isUserIdAvailable(thirdPartyUserName)) {
				signUpForm.setUserName(thirdPartyUserName);
			}
		}
		return signUpForm;
	}

	private boolean isUserNameValid(String userName, BindingResult errors) {
		if (userName == null || userName.trim().length() == 0) {
			errors.addError(new FieldError("signUpForm", "userName",
					"Please choose a username"));
			return false;
		} else {
			return true;
		}
	}

	private String signUpUser(ServletWebRequest request,
			SpringSocialSecurityProfile springSocialSecurityProfile,
			BindingResult errors) {
		String userName = springSocialSecurityProfile.getUserName();
		if (!isUserNameValid(userName, errors)) {
			return null;
		}
		if (!signUpService.isUserIdAvailable(userName)) {
			errors.addError(new FieldError("signUpForm", "userName",
					"Sorry, the username '" + userName + "' is not available"));
			return null;
		}
		try
		{
			signUpService.signUpUserAndCompleteConnection(
					springSocialSecurityProfile, request);
			return springSocialSecurityProfile.getUserName();
		}
		catch (UsernameAlreadyExistsException e)
		{
			errors.addError(new FieldError("signUpForm", "userName",
					"Sorry, the username '" + userName + "' is not available"));
			return null;
		}
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String signUpSubmit(
			ServletWebRequest request,
			@ModelAttribute("signUpForm") SpringSocialSecurityProfile signUpForm,
			BindingResult result) {
		Connection<?> connection = ProviderSignInUtils.getConnection(request);

		String userId = signUpUser(request, signUpForm, result);
		if (result.hasErrors() || userId == null) {
			return signUpView;
		}
		springSocialSecuritySignInService.signIn(userId, connection, request);
		return "redirect:" + authenticateUrl;

	}

}
