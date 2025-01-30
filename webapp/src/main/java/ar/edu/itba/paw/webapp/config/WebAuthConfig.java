package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.RedirectStrategy;

import java.util.concurrent.TimeUnit;

@EnableWebSecurity
@ComponentScan({
        "ar.edu.itba.paw.webapp.auth"
})
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Value("${rememberMe.key}")
    private String rememberMeKey;
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
        http    .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions!
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
                .formLogin().disable()
                .authorizeHttpRequests().requestMatchers("/api/**").permitAll();

       /*         .and()
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
}
