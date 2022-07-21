package com.zeldaspeedruns.zeldaspeedruns.security;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests(requests -> {
                    requests.mvcMatchers("/user/account/**").authenticated()
                            .anyRequest().permitAll();
                })
                .formLogin(formLogin -> {
                    formLogin.loginPage("/user/login");
                })
                .logout(logout -> {
                    logout.logoutUrl("/user/logout");
                    logout.clearAuthentication(true);
                })
                .oauth2Login(oauth2 -> {
                    oauth2.loginPage("/user/login");
                    oauth2.authorizationEndpoint(endpoint -> {
                        endpoint.baseUri("/user/login/oauth2/authorization");
                    });
                    oauth2.redirectionEndpoint(endpoint -> {
                        endpoint.baseUri("/user/login/oauth2/callback/*");
                    });
                });

        return httpSecurity.build();
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public SpringCacheBasedAclCache aclCache(CacheManager cacheManager) {
        var cache = cacheManager.getCache("acl");
        return new SpringCacheBasedAclCache(cache, permissionGrantingStrategy(), aclAuthorizationStrategy());
    }

    @Bean
    public LookupStrategy lookupStrategy(DataSource dataSource, AclCache aclCache, AclAuthorizationStrategy strategy) {
        return new BasicLookupStrategy(dataSource, aclCache, strategy, new ConsoleAuditLogger());
    }

    @Bean
    public JdbcMutableAclService aclService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
        return new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(AclService aclService) {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        var aclPermissionEvaluator = new AclPermissionEvaluator(aclService);
        expressionHandler.setPermissionEvaluator(aclPermissionEvaluator);
        return expressionHandler;
    }
}
