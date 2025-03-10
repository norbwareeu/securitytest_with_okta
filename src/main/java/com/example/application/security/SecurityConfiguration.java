package com.example.application.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configures Spring Security using VaadinWebSecurity helper.
 * <br><br>
 *
 * VaadinWebSecurity provides basic Vaadin security
 * configuration for the project out of the box. It sets up security rules for a
 * Vaadin application and restricts all URLs except for public resources and
 * internal Vaadin URLs to authenticated user.<br><br>
 *
 * In this class, we only need to alter the {@code HttpSecurity}
 * configuration in order to configure authentication support using an OAuth 2.0.
 *
 * @see "https://github.com/okta/samples-java-spring/blob/master/custom-login/src/main/java/com/okta/spring/example/HostedLoginCodeFlowExampleApplication.java"
 */
//@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {
    private static final String LOGIN_URL = "/custom-login";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http
                /*
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/custom-login", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )*/
                .exceptionHandling().accessDeniedHandler((req, res, e) -> res.sendRedirect("/403"))
                .and()
                .logout().logoutSuccessUrl("/")
                .and()
                .oauth2Client()
                .and()
                .oauth2Login().redirectionEndpoint().baseUri("/authorization-code/callback*");
/*
        http.oauth2Client(Customizer.withDefaults());
        http.oauth2Login(loginConf -> {
//            loginConf.loginPage(LOGIN_URL).permitAll();
            loginConf.redirectionEndpoint(redirectConf ->
                            redirectConf.baseUri("/authorization-code/callback*")
            );
        });*/
        setOAuth2LoginPage(http, LOGIN_URL);
    }
}

