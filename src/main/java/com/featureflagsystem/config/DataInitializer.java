package com.featureflagsystem.config;

import com.featureflagsystem.model.FeatureFlag;
import com.featureflagsystem.model.Role;
import com.featureflagsystem.model.User;
import com.featureflagsystem.repository.FeatureFlagRepository;
import com.featureflagsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FeatureFlagRepository featureFlagRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           FeatureFlagRepository featureFlagRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.featureFlagRepository = featureFlagRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed Admin User
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
        }

        // Seed Sample Feature Flags
        if (featureFlagRepository.count() == 0) {
            featureFlagRepository.save(FeatureFlag.builder()
                    .name("new-checkout-flow")
                    .description("Controls access to redesigned checkout experience")
                    .enabled(true)
                    .environment("PROD")
                    .rolloutPercentage(50)
                    .targetUserIds("user_101, user_102")
                    .build());

            featureFlagRepository.save(FeatureFlag.builder()
                    .name("dark-mode-v2")
                    .description("Next generation dark mode theme engine")
                    .enabled(true)
                    .environment("PROD")
                    .rolloutPercentage(100)
                    .build());

            featureFlagRepository.save(FeatureFlag.builder()
                    .name("beta-recommendation-engine")
                    .description("AI recommendation engine rollout for staging test users")
                    .enabled(false)
                    .environment("STAGING")
                    .rolloutPercentage(20)
                    .targetUserIds("tester_1, tester_2")
                    .build());
        }

        // Print application URLs directly to the terminal for easy clicking
        System.out.println("\n" +
                "========================================================================\n" +
                "🚀 Feature Flag & Experimentation System is Running!\n" +
                "========================================================================\n" +
                "  🎛️  Admin Console:       http://localhost:8081/index.html\n" +
                "      Login Credentials:   admin / admin123\n\n" +
                "  🧪  Evaluation Demo:     http://localhost:8081/demo.html\n\n" +
                "  📖  Swagger API Docs:    http://localhost:8081/swagger-ui.html\n\n" +
                "  🛢️  H2 Console:          http://localhost:8081/h2-console\n" +
                "========================================================================\n");
    }
}
