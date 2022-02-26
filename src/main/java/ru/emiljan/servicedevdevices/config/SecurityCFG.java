package ru.emiljan.servicedevdevices.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.emiljan.servicedevdevices.services.MyUserDetailsService;

/**
 * @author EM1LJAN
 */
@Configuration
@EnableWebSecurity
public class SecurityCFG extends WebSecurityConfigurerAdapter {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public SecurityCFG(BCryptPasswordEncoder bCryptPasswordEncoder,
                       MyUserDetailsService userDetailsService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/").permitAll()
                    .antMatchers("/orders/**").authenticated()
                    .antMatchers("/users/orders").authenticated()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/manager/**").hasAnyRole("ADMIN","MANAGER")
                .and()
                    .formLogin().loginPage("/users/login")
                    .usernameParameter("login")
                    .passwordParameter("password")
                .and()
                    .logout().logoutSuccessUrl("/users/login?logout").permitAll();

    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }

}
