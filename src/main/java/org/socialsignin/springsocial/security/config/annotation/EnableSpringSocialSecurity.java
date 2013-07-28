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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;

/**
 * Annotation to enable SoundCloud in a Spring Social application.
 * Configures a {@link SoundCloudConnectionFactory} bean (and a {@link ConnectionFactoryLocator} bean if one isn't already registered).
 * Also configures a request-scoped {@link SoundCloud} bean fetched from the current user's {@link ConnectionRepository}. 
 * @author Michael Lavelle
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(SpringSocialSecurityProviderConfigRegistrar.class)
public @interface EnableSpringSocialSecurity {

}
