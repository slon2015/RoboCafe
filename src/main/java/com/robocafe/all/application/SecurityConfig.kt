package com.robocafe.all.application

import com.robocafe.all.application.security.JwtFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.RequestMatcherEditor


@Configuration
@EnableWebSecurity
class SecurityConfig @Autowired constructor(
        private val jwtFilter: JwtFilter
): WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .httpBasic().disable()
                .formLogin().disable()
                .headers()
                    .frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/admin/tables").hasAuthority("tables_managment")
                    .antMatchers("/workers/**").hasRole("worker")
                    .antMatchers(HttpMethod.POST,"/workers/kitchen/**").hasAuthority("kitchen_managment")
                    .antMatchers(HttpMethod.GET, "/workers/kitchen/view/**").hasAuthority("kitchen_view")
                    .antMatchers(HttpMethod.POST,"/workers/hall/**").hasAuthority("hall_managment")
                    .antMatchers(HttpMethod.GET, "/workers/hall/view/**").hasAuthority("hall_view")
                    .antMatchers("/tables/**").hasRole("table")
                    .anyRequest().permitAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}