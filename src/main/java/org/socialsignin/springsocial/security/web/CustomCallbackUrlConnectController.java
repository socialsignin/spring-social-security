/*
 * Copyright 2014 the original author or authors.
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Custom version of ConnectController which is a workaround for the issue of the ConnectController's
 * callback path not being customisable.
 * 
 * Some providers such as SoundCloud only allow a single callback url to be registered for an application which means
 * that it's not possible to callback to both ProviderSignInController and ConnectController in a single application as 
 * they map to different urls.
 * 
 * To work around this we can use the ProviderSignInOrConnectController to act as a front controller for the single url
 * ( "/signInOrConnect/[providerId]") enabled by the ProviderSignInOrConnectController.
 * 
 * To use this strategy we must also use this customised ConnectController to amend the "redirect_uri" parameter so that
 * it can be set to /signInOrConnect instead of /connect for providers such as SoundCloud.
 * 
 * This controller is used so that a single callback url ("/signinOrConnect")
 * can be registered with providers which require exact match return urls and
 * which only allow one such url (eg. SoundCloud).
 * 
 * @author Michael Lavelle
 */
public class CustomCallbackUrlConnectController extends ConnectController {

	private static String DEFAULT_CONNECT_CALLBACK_BASE_PATH = "/connect";
	
	private Map<String,String> overridenConnectCallbackBasePathsByProviderId = new HashMap<String,String>();
	
	public CustomCallbackUrlConnectController(
			ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		super(connectionFactoryLocator, connectionRepository);
	}
	
	/**
	 * @param providerId
	 * @param callbackBasePath An alternate callback path for this provider (eg. "/signinOrConnect" instead of the default "/connect" )
	 */
	public void setOverriddenConnectCallbackBasePath(String providerId,String callbackBasePath)
	{
		overridenConnectCallbackBasePathsByProviderId.put(providerId, callbackBasePath);
	}
	
	private String getEncodedProviderCallbackPath(String providerId,String baseCallbackPath)
	{
		try {
			String encodedPath = URLEncoder.encode(baseCallbackPath + "/" + providerId,"UTF-8");
			return encodedPath;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@RequestMapping(value="/{providerId}", method=RequestMethod.POST)
	public RedirectView connect(@PathVariable String providerId, NativeWebRequest request) {
	
		RedirectView redirectView = super.connect(providerId, request);
		if (overridenConnectCallbackBasePathsByProviderId.containsKey(providerId))
		{
			String redirectUrl = redirectView.getUrl();

			// Modify the redirect url to specify an alternate callback base path for the provider
			redirectUrl = redirectUrl.replaceAll(getEncodedProviderCallbackPath(providerId,DEFAULT_CONNECT_CALLBACK_BASE_PATH), getEncodedProviderCallbackPath(providerId,overridenConnectCallbackBasePathsByProviderId.get(providerId)));
			redirectView.setUrl(redirectUrl);
			
			return redirectView;
			
		}
		else
		{
			return redirectView;
		}
	}

}
