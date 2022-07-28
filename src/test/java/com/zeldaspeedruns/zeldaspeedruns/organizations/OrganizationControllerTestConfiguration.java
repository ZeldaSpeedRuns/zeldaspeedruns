package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.MethodSecurityConfig;
import com.zeldaspeedruns.zeldaspeedruns.security.SecurityConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({SecurityConfig.class, MethodSecurityConfig.class})
public class OrganizationControllerTestConfiguration {
    private final OrganizationService organizationService;

    public OrganizationControllerTestConfiguration(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Bean
    public OrganizationPermissionEvaluator permissionEvaluator() {
        return new OrganizationPermissionEvaluator(organizationService);
    }
}
