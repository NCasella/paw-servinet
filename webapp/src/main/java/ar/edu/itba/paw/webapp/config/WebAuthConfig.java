package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        http.sessionManagement()
            .and()
            .authorizeRequests()
                .antMatchers("/login", "/registrarse", "/olvide-mi-clave", "/restablecer-clave/**").anonymous()
                .antMatchers("/perfil","/contratar-servicio/{serviceId}","/preguntar/**","/editar-opinion/**","/opinar/**").hasRole("USER")
                .antMatchers("/negocio/{businessID}/**","/borrar-negocio/{businessID}","/crear-servicio/{businessID}").access(" hasRole('BUSINESS') && @servinetAuthControl.isBusinessOwner(#businessID,@servinetAuthControl.currentUser.get().userId)")
                .antMatchers("/rechazar-turno/{appointmentId}","/turno/{serviceId}/{appointmentId}","/aceptar-turno/{appointmentId}","/cancelar-turno/{appointmentId}").access("hasRole('USER') && @servinetAuthControl.isUserAppointment(#appointmentId)")
                .antMatchers("/negocios/**","/publicar","/crear-servicio").hasRole("BUSINESS")
                .antMatchers("/servicios/**").permitAll()
                .antMatchers("/servicio/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/**").authenticated().and()
            .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", false) .and()
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
    }
    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers("/resources/**", "/images/**","/css/**", "/js/**", "/img/**", "/favicon.ico","/404","/403","/500");
    }
}
