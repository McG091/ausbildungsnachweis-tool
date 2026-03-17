package de.gfn.mannheim.ausbildungsnachweis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// Konfigurationsklasse: wird einmal beim Start geladen
// Legt fest wer was sehen darf (Sicherheitsregeln)
@Configuration
public class SecurityConfig {

    // Definiert die Sicherheitsregeln für alle HTTP-Anfragen
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Login-Seite und WebJars (Bootstrap) ohne Login erreichbar
                        .requestMatchers("/login", "/webjars/**").permitAll()
                        .anyRequest().authenticated() // Alles andere nur nach Login
                )
                .formLogin(form -> form
                        .loginPage("/login")               // Eigene Login-Seite verwenden
                        .loginProcessingUrl("/login")      // Spring verarbeitet das Formular hier
                        .defaultSuccessUrl("/", true)      // Nach Login → Startseite
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // Nach Logout → Login-Seite
                        .permitAll()
                );
        return http.build();
    }

    // Legt den Benutzer fest
    // Benutzername: azubi | Passwort: azubi123
    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("azubi")
                        .password(encoder.encode("azubi123")) // Passwort wird verschlüsselt
                        .roles("USER")
                        .build()
        );
    }

    // BCrypt: sicherer Algorithmus zum Verschlüsseln von Passwörtern
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}