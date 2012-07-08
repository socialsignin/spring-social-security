package org.socialsignin.springsocial.security.connect.support;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.connect.SpringSocialSecurityServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.AbstractConnection;

public class SpringSocialSecurityConnection extends
		AbstractConnection<SpringSocialSecurity> {

	private SpringSocialSecurityServiceProvider serviceProvider;
	private String password;

	public SpringSocialSecurityConnection(
			SpringSocialSecurityServiceProvider serviceProvider,
			ApiAdapter<SpringSocialSecurity> apiAdapter) {
		super(apiAdapter);
		this.serviceProvider = serviceProvider;
	}

	public SpringSocialSecurityConnection(
			SpringSocialSecurityServiceProvider serviceProvider,
			ConnectionData data, ApiAdapter<SpringSocialSecurity> apiAdapter) {
		super(data, apiAdapter);
		this.serviceProvider = serviceProvider;
		this.password = data.getAccessToken();
	}

	@Override
	public SpringSocialSecurity getApi() {
		synchronized (getMonitor()) {
			return serviceProvider.getSpringSocialSecurity(createData());
		}
	}

	public ConnectionData createData() {
		synchronized (getMonitor()) {
			return new ConnectionData(getKey().getProviderId(), getKey()
					.getProviderUserId(), getDisplayName(), getProfileUrl(),
					getImageUrl(), password, null, null, null);
		}
	}

}
