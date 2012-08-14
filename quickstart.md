Spring-Social-Security Quickstart
=================================

Core setup
----------

- Add repository and dependency to your project

```
<repositories>
   <repository>
	<id>opensourceagility-snapshots</id>
	<url>http://repo.opensourceagility.com/snapshots</url>
   </repository>
</repositories>
```
```
  	<dependency>
			<groupId>org.socialsignin</groupId>
			<artifactId>spring-social-security</artifactId>
			<version>1.0.2-SNAPSHOT</version>
	</dependency>
```
- Component-scan for spring-social-security components in both your application context and in your mvc context

```
	<context:component-scan base-package="org.socialsignin.springsocial.security" />
```
- Configure your spring security setup with a SpringSocialSecurityAuthenticationFilter in place of a form-login filter

```
 <http auto-config="false" 
    	<custom-filter position="FORM_LOGIN_FILTER" ref="springSocialSecurityAuthenticationFilter" />
```
- Create a page in your webapp which contains all the socialsignin buttons for login and which submits to spring-social's 
  ProviderSignInController ( default urls are "/signup/[providerid]" ).  Create an entry point in your security configuration
  for this page and set as the entry-point-ref on your security config.
```
<bean id="springSocialSecurityEntryPoint" 
class="org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint">
     <property name="loginFormUrl" value="/sociallogin"/>
</bean>
```

```
 <http auto-config="false" entry-point-ref="springSocialSecurityEntryPoint" 
    	<custom-filter position="FORM_LOGIN_FILTER" ref="springSocialSecurityAuthenticationFilter" />
```

Configuring your application for Sign-Up/Sign-In
------------------------------------------------

- Configure ProviderSignInController with "/authenticate" as the postSignInUrl (the default url for the SpringSocialSecurityAuthenticationFilter)
and set its signUpUrl to be "/signup" (the default url of SpringSocialSecuritySignUpController)

```
<bean class="org.springframework.social.connect.web.ProviderSignInController" >
    	<property name="signUpUrl" value="/signup" />
        <property name="postSignInUrl" value="/authenticate" />
   </bean>
```
- Create a view in your webapp which handles the choice of username by a user and submits to "/signup" 
  ( the default url of SpringSocialSecuritySignUpController )
- Set the following environment properties in your application

```
socialsignin.signUpView=(name of your choose username view)
socialsignin.defaultAuthenticationSuccessUrl=(url to send users after login)
```

- Optionally, configure your UsersConnectionRepository with SpringSocialSecurityConnectionSignUp to allow user local account
   and username selection to happen implicitly where possible, based on connection details from 3rd party provider

```
<bean id="usersConnectionRepository"
		class="org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository">
		<constructor-arg ref="dataSource" />
		<constructor-arg ref="connectionFactoryRegistry" />
		<constructor-arg ref="textEncryptor" />
		<property name="connectionSignUp" ref="springSocialSecurityConnectionSignUp" /> 
</bean>
```
  
Enabling futher connection options for logged-in users
------------------------------------------------------

- When users who have logged in with one provider wish to connect with an additional provider using your application,
spring-social-security peforms two functions through the use of ConnectInterceptors.

1)  Ensures that no other local user has connected using this provider account previously, as we use 3rd party
connection as a means of uniquely identifying a user.

2)  Amends the user's authorisation so they are granted provider-specific roles according to the set of providers
they have connected with.

To enable this functionality

* Create a subclass of SpringSocialSecurityConnectInterceptor for each provider you wish your users to be able to connect with
once they are logged in.
```
public class TwitterConnectInterceptor extends
		SpringSocialSecurityConnectInterceptor<Twitter> {

}
```
* Register these connect interceptors with ConnectController

Protecting resources using Spring Social Security
-------------------------------------------------

- To protect resources in your application, simply add intercept-urls to your security config as normal

```
		<intercept-url pattern="/protected/*" access="hasRole('ROLE_USER')" />
```

- If you wish to take advantage of the provider-specific roles that are granted to users of a spring-social-security app,
you can protect urls with rules such as 

```
		<intercept-url pattern="/protected/twitter" access="hasRole('ROLE_USER_TWITTER')" />
```

-- To enable provider-specific access denied handling, add SpringSocialSecurityAccessDeniedHandler to your security setup

```
        <access-denied-handler ref="springSocialSecurityAccessDeniedHandler"/>
```
This handler will attempt to determine a provider which the user needs to connect with to be granted
access to provider-protected resources, and if this can be determined, the user with be directed to
the spring-social provider-specific connection view.  To set a default access denied url in case this can't be 
determined, set the following property in your application.

```
socialsignin.defaultAccessDeniedUrl=
```