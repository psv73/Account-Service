package account.configuration;

import account.component.RestAuthenticationEntryPoint;
import account.constant.AppPath;
import account.constant.RoleEnum;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
    @Order(1)
    SecurityFilterChain h2ConsoleChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(PathRequest.toH2Console())
                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(h -> h.authenticationEntryPoint(restAuthenticationEntryPoint))
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler()))
                .csrf(csrf -> csrf.ignoringRequestMatchers(AppPath.API + "/**"))
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AppPath.USER + "/**").hasRole(RoleEnum.ROLE_ADMINISTRATOR.getRole())
                        .requestMatchers(HttpMethod.GET, AppPath.PAYMENT)
                        .hasAnyRole(RoleEnum.ROLE_ACCOUNTANT.getRole(), RoleEnum.ROLE_USER.getRole())
                        .requestMatchers(HttpMethod.GET, AppPath.SECURITY_EVENT).hasRole(RoleEnum.ROLE_AUDITOR.getRole())
                        .requestMatchers(HttpMethod.POST, AppPath.PAYMENTS).hasRole(RoleEnum.ROLE_ACCOUNTANT.getRole())
                        .requestMatchers(HttpMethod.PUT, AppPath.PAYMENTS).hasRole(RoleEnum.ROLE_ACCOUNTANT.getRole())
                        .requestMatchers(HttpMethod.POST, AppPath.CHANGE_PASS).authenticated()
                        .requestMatchers(HttpMethod.POST, AppPath.SIGN_UP).permitAll()
                        .requestMatchers(HttpMethod.POST, AppPath.ACTUATOR_SHUTDOWN).permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
