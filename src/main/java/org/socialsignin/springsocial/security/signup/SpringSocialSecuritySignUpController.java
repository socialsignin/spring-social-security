package org.socialsignin.springsocial.security.signup;

import org.socialsignin.springsocial.security.api.SpringSocialProfile;
import org.socialsignin.springsocial.security.api.SpringSocialSecurityProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/signup")
public class SpringSocialSecuritySignUpController extends AbstractSignUpController<SpringSocialProfile,SpringSocialSecuritySignUpService,SpringSocialSecurityProfileFactory>{

	
}
