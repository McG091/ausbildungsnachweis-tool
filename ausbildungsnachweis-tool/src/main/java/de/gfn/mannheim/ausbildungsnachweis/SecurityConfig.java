package de.gfn.mannheim.ausbildungsnachweis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Sicherheitskonfiguration: Login, Logout und Zugriffsregeln
@Configuration
public class SecurityConfig {

    private final AppUserRepository userRepo;

    public SecurityConfig(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Definiert die Sicherheitsregeln für alle HTTP-Anfragen
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Setup- und Login-Seite sowie WebJars ohne Login erreichbar
                        .requestMatchers("/setup", "/login", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    // Lädt den Benutzer aus der MySQL-Datenbank statt aus dem Arbeitsspeicher
    // Spring Security ruft diese Methode automatisch beim Login auf
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepo.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())  // bereits BCrypt-verschlüsselt
                        .roles("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Benutzer nicht gefunden: " + username));
    }

    // BCrypt: sicherer Algorithmus zum Verschlüsseln von Passwörtern
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}