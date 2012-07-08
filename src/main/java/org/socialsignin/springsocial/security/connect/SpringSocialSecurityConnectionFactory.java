package org.socialsignin.springsocial.security.connect;

import javax.annotation.PostConstruct;

import org.socialsignin.springsocial.security.api.SpringSocialSecurity;
import org.socialsignin.springsocial.security.connect.support.SpringSocialSecurityConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.stereotype.Service;

@Service
public class SpringSocialSecurityConnectionFactory extends
		ConnectionFactory<SpringSocialSecurity> {

	public final static String SPRING_SOCIAL_SECURITY_PROVIDER_NAME = "springSocialSecurity";
	
	@Autowired
	private ConnectionFactoryRegistry connectionFactoryRegistry;

	public SpringSocialSecurityConnectionFactory() {
		super(SPRING_SOCIAL_SECURITY_PROVIDER_NAME,
				new SpringSocialSecurityServiceProvider(),
				new SpringSocialSecurityAdapter());
	}

	@PostConstruct
	public void registerWithConnectionFactoryRegistry() {
		connectionFactoryRegistry.addConnectionFactory(this);
	}

	@Override
	public Connection<SpringSocialSecurity> createConnection(ConnectionData data) {
		return new SpringSocialSecurityConnection(
				(SpringSocialSecurityServiceProvider) getServiceProvider(),
				data, getApiAdapter());

	}

}
