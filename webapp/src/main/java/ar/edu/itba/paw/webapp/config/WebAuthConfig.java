package ar.edu.itba.paw.webapp.config;


import ar.edu.itba.paw.webapp.auth.filters.AuthEntryPoint;
import ar.edu.itba.paw.webapp.auth.filters.AuthFilter;
import ar.edu.itba.paw.webapp.auth.filters.DeniedEntryPoint;
import ar.edu.itba.paw.webapp.auth.filters.JwtFilter;
import ar.edu.itba.paw.webapp.auth.AuthorizationDecider;
import ar.edu.itba.paw.webapp.auth.filters.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
@ComponentScan({
        "ar.edu.itba.paw.webapp.auth"
})
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthFilter basicAuthFilter;
    @Autowired
    private JwtRefreshFilter jwtRefreshFilter;

    @Value("${SPA_BASE_URL}")
    private String SPA_ORIGIN;

    @Autowired
    private AuthorizationDecider authDecider;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable() // Disable CSRF for APIs
                .exceptionHandling().authenticationEntryPoint(new AuthEntryPoint()).accessDeniedHandler(new DeniedEntryPoint()).and()
                .addFilterBefore(basicAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtRefreshFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests()

                .requestMatchers("/api").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/images/{imageId:\\d+}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/images").hasRole("USER")
                .requestMatchers("/api/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/services/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/businesses/{businessId:\\d+}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/questions").permitAll()


                .requestMatchers(HttpMethod.GET,"/api/users/{userId:\\d+}").access(authDecider::canViewUserContactInfo)
                .requestMatchers(HttpMethod.PATCH,"/api/users/{userId:\\d+}").access(authDecider::canChangePassword)
                .requestMatchers("/api/users/{userId:\\d+}").access(authDecider::isCurrentUser)

                .requestMatchers(HttpMethod.POST, "/api/businesses").hasRole("USER")
                .requestMatchers("/api/businesses/{businessId:\\d+}").access(authDecider::isCurrentUserBusinessOwner)
                .requestMatchers(HttpMethod.POST,"/api/questions","/api/reviews").hasRole("USER")

                .requestMatchers(HttpMethod.PATCH,"/api/questions/{questionId:\\d+}").access(authDecider::canRespondQuestion)
                .requestMatchers("/api/services/{serviceId:\\d++}").access(authDecider::canChangeService)

                .requestMatchers(HttpMethod.GET,"/api/appointments").access(authDecider::canViewAppointmentList)
                .requestMatchers(HttpMethod.POST,"/api/appointmnets").hasRole("USER")
                .requestMatchers(HttpMethod.GET,"/api/appointments/{appointmentId:\\d+}").access(authDecider::canViewAppointment)
                .requestMatchers(HttpMethod.PATCH,"/api/appointments/{appointmentId:\\d+}").access(authDecider::canViewAppointment)

                .requestMatchers("/**").permitAll();
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().requestMatchers("/resources/**", "/images/**","/css/**", "/js/**", "/img/**", "/favicon.ico","/400","/404","/403","/500");
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new AuthEntryPoint();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(SPA_ORIGIN));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.addAllowedHeader("*");
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Authorization-access-token", "Link", "Location", "ETag", "X-Total-Count", "Authorization-Refresh-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
