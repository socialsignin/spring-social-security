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
package org.socialsignin.springsocial.security.signin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.socialsignin.springsocial.security.userauthorities.UserAuthoritiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * An AccessDeniedHandler which attempts to determine a providerid which would give a user access to a given url.  If this determination can be made, this
 * handler forwards the user to the connection page for this provider.
 * 
 * @author Michael Lavelle
 */
public class SpringSocialSecurityAccessDeniedHandler extends
		AccessDeniedHandlerImpl {

	@Autowired
	private SpringSocialSecurityAuthenticationFactory springSocialSecurityAuthenticationFactory;
	
	@Autowired
	private UserAuthoritiesService userAuthoritiesService;
	
	@Autowired
	private ConnectionFactoryRegistry connectionFactoryRegistry;
	
	@Override
	public void handle(HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException,
			ServletException {
		
		// Attempt to determine a set of provider ids which are required for this request which the current user has not yet connected with
		Set<String> requiredProviderIds = getRequiredProviderIds(request);
		if (requiredProviderIds != null && !requiredProviderIds.isEmpty())
		{
			// If we have found a set of provider ids which the user needs to connect with for this request, select one of them and send the user to the connect/<provider> page
			AccessDeniedHandlerImpl providerSpecificAccessDeniedHandler
			 = new AccessDeniedHandlerImpl();
			providerSpecificAccessDeniedHandler.setErrorPage("/connect/" + requiredProviderIds.iterator().next());
			providerSpecificAccessDeniedHandler.handle(request, response, accessDeniedException);
		}
		else
		{
			super.handle(request, response, accessDeniedException);

		}
	}

	
	protected List<Set<String>> getCombinationsOfAdditionalProviderIds()
	{
		List<Set<String>> listOfCombinationsOfAdditionalProviderIds = new ArrayList<Set<String>>();
		Authentication existingAuthentication = SecurityContextHolder.getContext().getAuthentication();
		Set<String> unconnectedProviders = new HashSet<String>();
		for (String registeredProviderId : connectionFactoryRegistry.registeredProviderIds())
		{
			GrantedAuthority providerAuthority = userAuthoritiesService.getProviderAuthority(registeredProviderId);
			if (existingAuthentication == null || !existingAuthentication.getAuthorities().contains(providerAuthority))
			{
				unconnectedProviders.add(registeredProviderId);
			}
		}
		// Just return list of single-provider combinations for now
		// TODO Add full combinations
		for (String unconnectedProvider : unconnectedProviders)
		{
			Set<String> singleProviderAddition = new HashSet<String>();
			singleProviderAddition.add(unconnectedProvider);
			listOfCombinationsOfAdditionalProviderIds.add(singleProviderAddition);
		}
		return listOfCombinationsOfAdditionalProviderIds;
	}
	
	protected Set<String> getRequiredProviderIds(HttpServletRequest request) throws ServletException
	{
		Authentication existingAuthentication = SecurityContextHolder.getContext().getAuthentication();
		Set<String> requiredProviderIds = null;
		for (Set<String> additionProviderIdsCombination  : getCombinationsOfAdditionalProviderIds())
		{
			if (requiredProviderIds == null)
			{
				boolean providerCombinationAllowsAccess = getPrivilegeEvaluator(request).isAllowed(request.getContextPath(),getUri(request),request.getMethod(), springSocialSecurityAuthenticationFactory.updateAuthenticationForNewProviders(existingAuthentication, additionProviderIdsCombination));
				if (providerCombinationAllowsAccess)
				{
					requiredProviderIds = additionProviderIdsCombination;
				}
			}
		}
		return requiredProviderIds;
	}
	
	public String getUri(HttpServletRequest request)
	{
		return request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
	}
	
	protected Authentication createAuthenticationForAdditionalProvider(String providerId)
	{
		Authentication existingAuthentication = SecurityContextHolder.getContext().getAuthentication();
		return springSocialSecurityAuthenticationFactory.updateAuthenticationForNewProvider(existingAuthentication, providerId);
	}
	
	protected Authentication createAuthenticationForAdditionalProviders(Collection<String> providerIds)
	{
		Authentication existingAuthentication = SecurityContextHolder.getContext().getAuthentication();
		Authentication updatedAuthentication = existingAuthentication;
		for (String providerId : providerIds)
		{
			updatedAuthentication = springSocialSecurityAuthenticationFactory.updateAuthenticationForNewProvider(updatedAuthentication, providerId);
		}
		return updatedAuthentication;
	}
	
	
	protected WebInvocationPrivilegeEvaluator getPrivilegeEvaluator(HttpServletRequest request) throws ServletException  {
        ServletContext servletContext = request.getSession().getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        Map<String, WebInvocationPrivilegeEvaluator> wipes = ctx.getBeansOfType(WebInvocationPrivilegeEvaluator.class);

        if (wipes.size() == 0) {
            throw new ServletException("No visible WebInvocationPrivilegeEvaluator instance could be found in the application " +
                    "context. There must be at least one in order to support the use of URL access checks in this AccessDeniedHandler.");
        }

        return (WebInvocationPrivilegeEvaluator) wipes.values().toArray()[0];
    }

	
	
}
