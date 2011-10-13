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
package org.socialsignin.springframework.social.security.signup;

import org.socialsignin.springframework.social.security.signin.SpringSocialSecuritySignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for displaying/procesing a sign up form and handling chosen user id, checking
 * if it already exists
 *
* @author Michael Lavelle
*/
@Controller
@RequestMapping("/signup")
public class SpringSocialSecuritySignUpController {

	@Value("${socialsignin.signUpView}")
	private String signUpView;
	
	@Autowired
	private SpringSocialSecuritySignInService springSocialSecuritySignInService;
	
	@Autowired
	private UserDetailsService springSocialSecurityUserDetailsService;
	
	@RequestMapping(value="",method=RequestMethod.GET)
	public ModelAndView signUpForm(ServletWebRequest request)
	{
		Connection<?> connection = ProviderSignInUtils.getConnection(request);
		return new ModelAndView(signUpView,"connectionData",connection.createData());
	}
	
	
	@RequestMapping(value="",method=RequestMethod.POST)
	public String signUpSubmit(ServletWebRequest request,@RequestParam("userId") String userId)
	{
		try
		{
			springSocialSecurityUserDetailsService.loadUserByUsername(userId);
			// TODO Error messages
			return signUpView;
		}
		catch (UsernameNotFoundException e)
		{

			Connection<?> connection = ProviderSignInUtils.getConnection(request);
			ProviderSignInUtils.handlePostSignUp(userId, request);
			springSocialSecuritySignInService.signIn(userId, connection, request);
			return "redirect:/authenticate";
		}

	}
	
}
