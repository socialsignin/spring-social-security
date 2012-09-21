package org.socialsignin.springsocial.security.signup;
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
import org.socialsignin.springsocial.security.api.SpringSocialProfile;

/**
 * Non-generic SignUpService interface implemented by default SignUpService implementations
 * which create minimal SpringSocialProfile data on signup, allowing such components to be autowired by type.
 * 
 * @author Michael Lavelle
 */
public interface SpringSocialSecuritySignUpService extends SignUpService<SpringSocialProfile> {

}
