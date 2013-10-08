/*
 * Copyright 2012 the original author or authors.
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
package org.socialsignin.springsocial.security.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Front controller which handles provider auth callback methods and forwards to
 * ProviderSignInController or ConnectController depending on user's logged in
 * status. For this strategy to work, the application must ensure that calls to
 * the ProviderSignInController (normally those for "/signin/[providerId]" urls
 * are only accessible to non-logged in users. This means that it is safe to
 * assume in this controller that a callback for a non-logged in user be
 * forwarded to the signin controller, while the callback for a logged-in user
 * be forwarded to the connect controller.
 * 
 * This controller is used so that a single callback url ("/signinOrConnect")
 * can be registered with providers which require exact match return urls and
 * which only allow one such url (eg. SoundCloud).
 * 
 * @author Michael Lavelle
 */
@Controller
@RequestMapping("/signinOrConnect")
public class ProviderSignInOrConnectController {

	private String signInPath = "/signin";
	private String connectPath = "/connect";
	
	@Autowired
	private UserIdSource userIdSource;
	
	
	protected boolean isUserSignedIn()
	{
		try
		{
			return userIdSource.getUserId() != null;
		}
		catch (IllegalStateException e)
		{
			return false;
		}
	}

	protected String getRedirectPath() {
		return  !isUserSignedIn() ? signInPath : connectPath;
	}

	public void setSignInPath(String signInPath) {
		this.signInPath = signInPath;
	}

	public void setConnectPath(String connectPath) {
		this.connectPath = connectPath;
	}

	/**
	 * Process the authentication callback from an OAuth 1 service provider.
	 * Called after the member authorizes the authentication, generally done
	 * once by having he or she click "Allow" in their web browser at the
	 * provider's site. Handles the provider sign-in callback by first
	 * determining if a local user account is associated with the connected
	 * provider account. If so, signs the local user in by delegating to
	 * {@link SignInAdapter#signIn(String, Connection, NativeWebRequest)} If
	 * not, redirects the user to a signup page to create a new account with
	 * {@link ProviderSignInAttempt} context exposed in the HttpSession.
	 * 
	 * @see ProviderSignInAttempt
	 * @see ProviderSignInUtils
	 */
	@RequestMapping(value = "/{providerId}", method = RequestMethod.GET, params = "oauth_token")
	public String oauth1Callback(
			@PathVariable String providerId,
			NativeWebRequest request,
			@RequestParam("oauth_token") String oauthToken,
			@RequestParam(value = "oauth_verifier", required = false) String oauthVerifier) {
		return "redirect:"
				+ getRedirectPath()
				+ "/"
				+ providerId
				+ "?oauth_token="
				+ oauthToken
				+ (oauthVerifier == null ? ""
						: ("&oauth_verifier=" + oauthVerifier));
	}

	/**
	 * Process the authentication callback from an OAuth 2 service provider.
	 * Called after the user authorizes the authentication, generally done once
	 * by having he or she click "Allow" in their web browser at the provider's
	 * site. Handles the provider sign-in callback by first determining if a
	 * local user account is associated with the connected provider account. If
	 * so, signs the local user in by delegating to
	 * {@link SignInAdapter#signIn(String, Connection, NativeWebRequest)}. If
	 * not, redirects the user to a signup page to create a new account with
	 * {@link ProviderSignInAttempt} context exposed in the HttpSession.
	 * 
	 * @see ProviderSignInAttempt
	 * @see ProviderSignInUtils
	 */
	@RequestMapping(value = "/{providerId}", method = RequestMethod.GET, params = "code")
	public String oauth2Callback(@PathVariable String providerId,
			@RequestParam("code") String code, NativeWebRequest request) {
		return "redirect:" + getRedirectPath() + "/" + providerId + "?code="
				+ code;

	}

}
