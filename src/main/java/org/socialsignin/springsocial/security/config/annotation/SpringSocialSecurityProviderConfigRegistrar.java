package org.socialsignin.springsocial.security.config.annotation;
/*
 * Copyright 2013 the original author or authors.
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

import java.util.Map;

import org.socialsignin.springsocial.security.SpringSocialSecurityAuthenticationService;
import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.config.support.SpringSocialSecurityApiHelper;
import org.socialsignin.springsocial.security.connect.SpringSocialSecurityConnectionFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.social.config.annotation.AbstractProviderConfigRegistrarSupport;
import org.springframework.social.security.provider.SocialAuthenticationService;



/**
 * {@link ImportBeanDefinitionRegistrar} for configuring a {@link SpringSocialSecurityConnectionFactory} bean and a request-scoped {@link SpringSocialSecurity} bean.
 * @author Michael Lavelle
 */
public class SpringSocialSecurityProviderConfigRegistrar extends AbstractProviderConfigRegistrarSupport {

	public SpringSocialSecurityProviderConfigRegistrar() {
		super(EnableSpringSocialSecurity.class,SpringSocialSecurityConnectionFactory.class, SpringSocialSecurityApiHelper.class);
	}

	
	@Override
	protected BeanDefinition getConnectionFactoryBeanDefinition(String appId,
			String appSecret, Map<String, Object> allAttributes) {
		return BeanDefinitionBuilder.genericBeanDefinition(connectionFactoryClass).getBeanDefinition();
	}
	

	@Override
	protected BeanDefinition getAuthenticationServiceBeanDefinition(
			String appId, String appSecret, Map<String, Object> allAttributes) {
		return BeanDefinitionBuilder.genericBeanDefinition(authenticationServiceClass).getBeanDefinition();

	}


	@Override
	protected Class<? extends SocialAuthenticationService<?>> getAuthenticationServiceClass() {
		return SpringSocialSecurityAuthenticationService.class;
	}

	



}
