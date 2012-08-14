Spring-Social-Security Quickstart
---------------------------------

1. Core setup

* Add repository and dependency to your project
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
* Component-scan for spring-social-security components in both your application context and in your mvc context
```
	<context:component-scan base-package="org.socialsignin.springsocial.security" />
```
* Configure your spring security setup with a SpringSocialSecurityAuthenticationFilter in place of a form-login filter:
```
 <http auto-config="false" ...
    	<custom-filter position="FORM_LOGIN_FILTER" ref="springSocialSecurityAuthenticationFilter" />
	...
```