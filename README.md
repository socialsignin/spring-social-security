About Spring-Social-Security
============================

Many applications using Spring Security for authentication will need to

* Ask users to sign up using a username and password.
* Create their own user details store and data access objects
* Provide account management (eg. forgotten password functionality)
* Provide access control for protected resources

For applications which already use spring-social to connect with external authenticated apis (eg. Facebook, Twitter)
, SocialSignIn's spring-social-security module removes these authentication concerns by delegating authentication
in Spring Security via spring-social to the third party api. Authenticating your website users via spring-social api
providers means:

* No need for users to remember another password for another site.
* No need for developers to create their own user details store, as the store used by spring-social is used instead.
* No need for developers to provide account management as this is provided by the third party api.
* Provider-specific roles are granted to users on the basis of their connected providers, allowing fine-grained
permissioning model.

For simple "Hello World" apps demonstrating spring-social-security see:

* https://github.com/socialsignin/spring-social-security-demo  ( Basic integration with core spring-social )

* https://github.com/socialsignin/socialsignin-showcase ( Using SocialSignin provider modules for auto-registration of providers and API abstraction layer )

* https://github.com/socialsignin/socialsignin-roo-showcase ( Roo project with SocialSignin modules and Roo-backed persistence for UserConnections )

Also see <a href="http://socialsignin.org/spring-social-security/docs/1.0.2.RELEASE/api/">JavaDoc for Spring Social Security</a> for API docs.

Spring-Social-Security Quickstart
=================================

Adding Spring-Social-Security to a Spring-Social web application
----------------------------------------------------------------

- Add repository and dependency to your project

```
<repositories>
   <repository>
	<id>opensourceagility-releases</id>
	<url>http://repo.opensourceagility.com/releases</url>
   </repository>
</repositories>
```
```
  	<dependency>
			<groupId>org.socialsignin</groupId>
			<artifactId>spring-social-security</artifactId>
			<version>1.0.2.RELEASE</version>
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
- Create a page in your webapp ( <a href="https://github.com/socialsignin/spring-social-security-demo/blob/master/src/main/webapp/oauthlogin.jsp">example</a> ) which contains all the socialsignin buttons for login and which submits to spring-social's 
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
- Create a view in your webapp handles the choice of username by a user - this view will be served
by SpringSocialSecuritySignUpController under default url of "/signup" and will need to post username
back to this "/signup" url ( <a href="https://github.com/socialsignin/spring-social-security-demo/blob/master/src/main/webapp/WEB-INF/signUpForm.jsp">example</a>

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

- Spring Social's ConnectController allow users who have logged in with one provider to connect with an
another 3rd-party provider. spring-social-security peforms two functions to support this use-case with ConnectController
through the use of ConnectInterceptors.  These interceptors

* Ensure that no other local user has connected using this provider account previously, as we use 3rd party
connection as a means of uniquely identifying a user.

* Amend the user's authorisation so they are granted provider-specific roles according to the set of providers
they have connected with.

- To enable this functionality

* Create a subclass of SpringSocialSecurityConnectInterceptor for each provider you wish your users to be able to connect with
once they are logged in.

```
public class TwitterConnectInterceptor extends
		SpringSocialSecurityConnectInterceptor<Twitter> {

}
```

- Register these connect interceptors with ConnectController

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

- To enable provider-specific access denied handling, add SpringSocialSecurityAccessDeniedHandler to your security setup

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