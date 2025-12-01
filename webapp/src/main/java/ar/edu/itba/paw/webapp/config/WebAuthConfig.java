package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.auth.filters.BasicAuthFilter;
import ar.edu.itba.paw.webapp.auth.filters.AuthEntryPoint;
import ar.edu.itba.paw.webapp.auth.filters.DeniedEntryPoint;
import ar.edu.itba.paw.webapp.auth.filters.JwtFilter;
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
import java.util.Collections;
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
    private BasicAuthFilter basicAuthFilter;

    @Value("${SPA_BASE_URL}")
    private String SPA_ORIGIN;
/*
    @Value("${rememberMe.key}")
    private String rememberMeKey;
*/
    @Autowired
    private ServinetAuthControl authControl;

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
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET,"/api/users/{userId:\\d+}").permitAll()
                .requestMatchers("/api/users/{userId}").access(authControl::isCurrentUser)
                .requestMatchers("/api/users").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/businesses/{businessId:\\d+}").permitAll()
                .requestMatchers("/api/businesses/{businessId:\\d+}","/api/businesses/{businessId:\\d+}/statistics").access(authControl::isCurrentUserBusinessOwner)
                .requestMatchers(HttpMethod.POST,"/api/services/{serviceId:\\d+}/questions","/api/services/{serviceId:\\d+}/reviews").hasRole("USER")
                .requestMatchers(HttpMethod.GET,"/api/services/**").permitAll()
                .requestMatchers("/api/services/{serviceId:\\d++}").access(authControl::canChangeService)
                .requestMatchers("/api/appointments/{appointmentId:\\d+}").access(authControl::canViewAppointment)
                .requestMatchers("/api/").permitAll()
                .requestMatchers("/**").authenticated();

       /*        .and()
                .authorizeRequests()
                .requestMatchers("/login", "/registrarse", "/olvide-mi-clave", "/restablecer-clave/**", "/verificar-cuenta/**").anonymous()
                .requestMatchers("/editar-opinion/{serviceID:\\d+}/{ratingId:\\d+}").access("hasRole('USER') && @servinetAuthControl.isRatingOwner(#ratingId)")
                .requestMatchers("/perfil", "/contratar-servicio/{serviceId:\\d+}", "/preguntar/**", "/opinar/**").hasRole("USER")
                .requestMatchers("/negocio/{businessID:\\d+}/turnos","/negocio/{businessID:\\d+}/estadisticas", "/borrar-negocio/{businessID:\\d+}", "/crear-servicio/{businessID:\\d+}", "/{businessID:\\d+}/editar-negocio").access(" hasRole('BUSINESS') && @servinetAuthControl.isBusinessOwner(#businessID,@servinetAuthControl.currentUser.get().userId)")
                .requestMatchers("/borrar-servicio/{serviceId:\\d+}", "/editar-servicio/{serviceId:\\d+}").access("hasRole('BUSINESS') && @servinetAuthControl.isServiceOwner(#serviceId)")
                .requestMatchers("/rechazar-turno/{appointmentId:\\d+}","/aceptar-turno/{appointmentId:\\d+}", "/negocio/solicitud-turno/{appointmentId:\\d+}").access("hasRole('BUSINESS') && @servinetAuthControl.isAdminAppointment(#appointmentId)")
                .requestMatchers("/turno/{serviceId:\\d+}/{appointmentId:\\d+}", "/cancelar-turno/{appointmentId:\\d+}").access("hasRole('USER') && (@servinetAuthControl.isUserAppointment(#appointmentId) || @servinetAuthControl.isAdminAppointment(#appointmentId))")
                .requestMatchers("/negocios/**").hasRole("BUSINESS")
                .requestMatchers("/servicios/**").permitAll()
                .requestMatchers("/servicio/**").permitAll()
                .requestMatchers("/negocio/{businessID:\\d+}").permitAll()
                .requestMatchers("/negocio/opiniones/{businessID:\\d+}/**").permitAll()
                .requestMatchers("/").permitAll().
                requestMatchers("/**").authenticated().and()
            .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", false).failureHandler((request,response,exception)-> {
                    String url;
                    if (exception instanceof DisabledException) {
                        url="/login?notVerified";
                    }else{
                        url="/login?error";
                    }
                    response.sendRedirect(request.getContextPath()+url);
                }) .and()
            .rememberMe()
                .userDetailsService(userDetailsService)
                .rememberMeParameter("remember-me").key(rememberMeKey)
                .tokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(6)).and()
            .logout().logoutUrl("/logout").logoutSuccessUrl("/login").and()
                .exceptionHandling().accessDeniedHandler((request,response,accessDeniedException) ->{
                    if (request.getServletPath().contains("/negocios")) {
                        response.sendRedirect(request.getContextPath()+"/registrar-negocio");
                    }else {
                        response.sendRedirect(request.getContextPath()+"/403");
                    }
                }).and()
            .csrf().disable();
            */
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
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Link", "Location", "ETag", "X-Total-Count", "X-Refresh-Token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
