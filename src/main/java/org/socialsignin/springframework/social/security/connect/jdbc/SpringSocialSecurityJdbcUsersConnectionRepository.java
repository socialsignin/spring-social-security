package org.socialsignin.springframework.social.security.connect.jdbc;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;

/**
* Modified version of JdbcUsersConnectionRepository which can handle null user ids
* returned by ConnectionSignUp which indicate an account could not be created.
* 
* According to the ConnectionSignUp docs, a null is valid to pass back from ConnectionSignUp.execute
* - however current JdbcUsersConnectionRepository does not handle null values. 
* TODO - Fork JdbcUsersConnectionRepository to fix this
* 
* @author Michael Lavelle
*/
public class SpringSocialSecurityJdbcUsersConnectionRepository extends
		JdbcUsersConnectionRepository {

	private final JdbcTemplate jdbcTemplate;
	private String tablePrefix = "";
	private ConnectionSignUp connectionSignUp;

	public void setTablePrefix(String tablePrefix) {
		super.setTablePrefix(tablePrefix);
		this.tablePrefix = tablePrefix;
	}

	public SpringSocialSecurityJdbcUsersConnectionRepository(
			DataSource dataSource,
			ConnectionFactoryLocator connectionFactoryLocator,
			TextEncryptor textEncryptor) {
		super(dataSource, connectionFactoryLocator, textEncryptor);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	// Overriding setConnectionSignUp and findUserIds.... here as superclass
	// does not
	// handle null userids properly - fork in future
	public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
		this.connectionSignUp = connectionSignUp;
		super.setConnectionSignUp(connectionSignUp);
	}

	public List<String> findUserIdsWithConnection(Connection<?> connection) {
		ConnectionKey key = connection.getKey();
		List<String> localUserIds = jdbcTemplate
				.queryForList(
						"select userId from "
								+ tablePrefix
								+ "UserConnection where providerId = ? and providerUserId = ?",
						String.class, key.getProviderId(),
						key.getProviderUserId());
		if (localUserIds.size() == 0) {
			if (connectionSignUp != null) {
				String newUserId = connectionSignUp.execute(connection);
				if (newUserId != null) {
					createConnectionRepository(newUserId).addConnection(
							connection);
					return Arrays.asList(newUserId);
				}
			}
		}
		return localUserIds;
	}

}
