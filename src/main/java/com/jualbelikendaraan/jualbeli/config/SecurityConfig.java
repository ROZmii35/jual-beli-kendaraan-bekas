package com.jualbelikendaraan.jualbeli.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.config.Customizer;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.jualbelikendaraan.jualbeli.security.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**", "/admin-login", "/AdminDashboard")
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin-login", "/error", "/logout").permitAll()
                        .requestMatchers("/admin/**", "/AdminDashboard").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/admin-login")
                        .loginProcessingUrl("/admin-login")
                        .defaultSuccessUrl("/AdminDashboard", true)
                        .failureUrl("/admin-login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // Redirect ke halaman utama setelah logout
                );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(Customizer.withDefaults()) // Mengaktifkan CSRF
                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints
                        .requestMatchers("/", "/register", "/css/**", "/js/**", "/images/**", "/static/**", "/webjars/**", "/resources/**", "/error", "/logout", "/user-login", "/uploads/**").permitAll()

                        // Authenticated user endpoints
                        .requestMatchers("/my-account", "/profil", "/transaksi", "/profile").authenticated()
                        
                        // All other requests require authentication by default
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/user-login")
                        .loginProcessingUrl("/user-login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/user-login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // Redirect ke halaman utama setelah logout
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    // Konfigurasi user in-memory (contoh sederhana, dalam produksi gunakan UserDetailsService dari database)
    // @Bean
    // public UserDetailsService userDetailsService() {
    //     UserDetails user = User.withDefaultPasswordEncoder()
    //         .username("user")
    //         .password("password")
    //         .roles("USER")
    //         .build();
    //     UserDetails admin = User.withDefaultPasswordEncoder()
    //         .username("admin")
    //         .password("admin")
    //         .roles("ADMIN", "USER")
    //         .build();
    //     return new InMemoryUserDetailsManager(user, admin);
    // }

    // Mendefinisikan bean PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Hanya untuk development/testing, tidak direkomendasikan untuk produksi
    }
}