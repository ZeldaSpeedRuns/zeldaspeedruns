package com.zeldaspeedruns.zeldaspeedruns.security;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserRepository;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SuperuserCreatorRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(SuperuserCreatorRunner.class);

    private final ZsrUserService userService;
    private final ZsrUserRepository userRepository;

    @Value("${zeldaspeedruns.superuser.username:admin}")
    private String username;

    @Value("${zeldaspeedruns.superuser.email-address:admin@example.com}")
    private String emailAddress;

    @Value("${zeldaspeedruns.superuser.password}")
    private String password;

    @Value("${zeldaspeedruns.superuser.create-superuser:false}")
    private Boolean createSuperuser;

    public SuperuserCreatorRunner(ZsrUserService userService, ZsrUserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (createSuperuser && userRepository.count() == 0) {
            Objects.requireNonNull(username, "username must not be null");
            Objects.requireNonNull(emailAddress, "emailAddress must not be null");
            Objects.requireNonNull(password, "password must not be null");

            logger.info("Generating superuser with username '{}' and ADMIN role", username);
            var user = userService.createUser(username, emailAddress, password);
            user.setAdministrator(true);
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
}
