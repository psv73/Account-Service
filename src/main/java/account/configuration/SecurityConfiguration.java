package account.configuration;

import account.component.RestAuthenticationEntryPoint;
import account.model.enums.RoleEnum;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    public SecurityConfiguration(RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer
                        .authenticationEntryPoint(restAuthenticationEntryPoint))
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler()))  // Handle auth errors
                .csrf(AbstractHttpConfigurer::disable) // For Postman
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))   //For the H2 console
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin).disable()) // For the H2 console
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/user/**").hasRole(RoleEnum.ROLE_ADMINISTRATOR.getRole())
                        .requestMatchers(HttpMethod.GET, "/api/empl/payment")
                            .hasAnyRole(RoleEnum.ROLE_ACCOUNTANT.getRole(),
                                    RoleEnum.ROLE_USER.getRole())
                        .requestMatchers(HttpMethod.GET, "/api/security/events").hasRole(RoleEnum.ROLE_AUDITOR.getRole())
                        .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasRole(RoleEnum.ROLE_ACCOUNTANT.getRole())
                        .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole(RoleEnum.ROLE_ACCOUNTANT.getRole())
                        .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // no session

        return http.build();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
