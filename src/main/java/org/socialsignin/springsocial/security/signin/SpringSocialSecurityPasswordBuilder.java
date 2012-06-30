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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.social.connect.ConnectionData;

public class SpringSocialSecurityPasswordBuilder {

	private String firstAccessTokenAlphabetically;
	private boolean hasAnyConnectionExpired = false;
	
	public SpringSocialSecurityPasswordBuilder(List<ConnectionData> connectionDataList)
	{
		List<String> accessTokens = new ArrayList<String>();
		for (ConnectionData connectionData : connectionDataList)
		{
			if (hasConnectionExpired(connectionData))
			{
				hasAnyConnectionExpired = true;
			}
			else
			{
				accessTokens.add(connectionData.getAccessToken());
				Collections.sort(accessTokens);
				firstAccessTokenAlphabetically = accessTokens.get(0);
			}
		}
	}
	
	private boolean hasConnectionExpired(ConnectionData connectionData)
	{
		return connectionData.getExpireTime() != null && new Date().after(new Date(connectionData.getExpireTime()));
	}
	
	public SpringSocialSecurityPasswordBuilder(String existingAccessToken, ConnectionData connectionData)
	{
		if (hasConnectionExpired(connectionData))
		{
			hasAnyConnectionExpired = true;
		}
		firstAccessTokenAlphabetically = (connectionData.getAccessToken().compareTo(existingAccessToken) < 0) ? connectionData.getAccessToken() : existingAccessToken;
	}
	

	
	public String build()
	{
		return hasAnyConnectionExpired ? "" : firstAccessTokenAlphabetically;
	}
	
}
