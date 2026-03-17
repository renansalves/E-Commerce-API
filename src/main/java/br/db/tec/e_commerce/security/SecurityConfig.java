package br.db.tec.e_commerce.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Autowired
  private SecurityFilter securityFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            .requestMatchers("/swagger-ui.html").permitAll()
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/v3/api-docs/**").permitAll()
            .requestMatchers("/swagger-resources/**").permitAll()
            .requestMatchers("/webjars/**").permitAll().requestMatchers("/v3/api-docs").permitAll()

            .requestMatchers("/api/users/register").permitAll()
            .requestMatchers("/api/users/login").permitAll()
            .requestMatchers("/api/products/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
          res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          res.setContentType("application/json");
          res.getWriter().write("{\"error\":\"unauthorized\"}");
        }))
        .addFilterBefore(securityFilter,
            org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // Allow requests from a specific origin
    configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://yourfrontenddomain.com"));
    // Allow specific methods
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    // Allow all headers
    configuration.setAllowedHeaders(List.of("*"));
    // Allow credentials (cookies, HTTP authentication)
    configuration.setAllowCredentials(true);
    // Set max age for preflight requests cache
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // Apply this CORS config to all paths
    return source;
  }
}
