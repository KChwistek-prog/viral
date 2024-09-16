package com.med.viral;

import com.med.viral.model.security.RegisterRequest;
import com.med.viral.security.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.med.viral.model.security.Role.ADMIN;

@SpringBootApplication
public class ViralApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViralApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService service
    ) {
        return args -> {
            var admin = RegisterRequest.builder()
                    .firstname("Admin")
                    .lastname("Admin")
                    .email("admin@mail.com")
                    .password("password")
                    .role(ADMIN)
                    .build();
            System.out.println("Admin token: " + service.register(admin).accessToken());
        };
    }
}
