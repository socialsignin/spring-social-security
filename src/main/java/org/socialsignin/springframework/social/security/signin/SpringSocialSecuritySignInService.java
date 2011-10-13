package org.socialsignin.springframework.social.security.signin;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;


/**
Adapter that bridges between a ProviderSignInController and a SpringSocialSecuritySignInService. 
Invoked at the end of a provider sign-in attempt to sign-in the local user account associated with the provider user account. 

Places sign in details in the session so that they can be accessed by SpringSocialSecurityAuthenticationFilter

* @author Michael Lavelle
*/
@Service
public class SpringSocialSecuritySignInService implements SignInAdapter {
  
	public final static String SIGN_IN_DETAILS_SESSION_ATTRIBUTE_NAME = "org.socialsignin.springframework.social.security.signInDetails";
	
	public String signIn(String localUserId,Connection<?> connection,NativeWebRequest nativeWebRequest) {
    	
		nativeWebRequest.setAttribute(SIGN_IN_DETAILS_SESSION_ATTRIBUTE_NAME,
				new SpringSocialSecuritySignInDetails(localUserId,connection),RequestAttributes.SCOPE_SESSION);
		return null;
    }
}