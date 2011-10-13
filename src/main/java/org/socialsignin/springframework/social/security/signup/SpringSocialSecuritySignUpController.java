package org.socialsignin.springframework.social.security.signup;

import org.socialsignin.springframework.social.security.signin.SpringSocialSecuritySignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
