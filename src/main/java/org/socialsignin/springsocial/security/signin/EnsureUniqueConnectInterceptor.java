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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

/**
 * ConnectInterceptor which provides a workaround for an issue
 * which can occur in SpringSocial if a user attempts to 
 * connect their local account with a 3rd party connection already
 * associated with a different local account.
 * 
 * Although this scenario is an edge-case, if it does occur it
 * leaves spring-social in a state where both local user accounts
 * sharing the 3rd party connection are effectively locked, with
 * login to neither being possible, as the application can't
 * determine which local user account to login.
 * 
 * A provider-specific subclass must be created for each
 * registered provider and registered with spring-social
 * to handle this edge-case.
 * 
 * @author Michael Lavelle
 */
public class EnsureUniqueConnectInterceptor<S> implements ConnectInterceptor<S> {

	@Autowired
	private UsersConnectionRepository usersConnectionRepository;

	@Autowired
	private ConnectionRepository connectionRepository;

	@Override
	public void preConnect(ConnectionFactory<S> connectionFactory,
			MultiValueMap<String, String> parameters, WebRequest request) {

	}

	@Override
	public void postConnect(Connection<S> connection, WebRequest request) {

		boolean connectionAlreadyAssociatedWithAnotherUser = usersConnectionRepository
				.findUserIdsWithConnection(connection).size() > 1;
		if (connectionAlreadyAssociatedWithAnotherUser) {
			connectionRepository.removeConnection(connection.getKey());
			NonUniqueConnectionException nonUniqueConnectionException = new NonUniqueConnectionException(
					"The connection is already associated with a different account");
			request.setAttribute("lastSessionException",
					nonUniqueConnectionException, WebRequest.SCOPE_SESSION);
			throw nonUniqueConnectionException;
		}

	}

}
