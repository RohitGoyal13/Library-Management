package com.rohit.library.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                // ALLOW Auth and Actuator endpoints
                it.requestMatchers("/auth/**", "/actuator/**").permitAll()
                // LOCK everything else
                it.anyRequest().authenticated()
            }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }

        return http.build()
    }

    //  THIS BEAN REMOVES THE "GENERATED PASSWORD" LOG
    @Bean
    fun userDetailsService(): UserDetailsService {
        val user = User.withUsername("admin")
            .password("{noop}password") // {noop} means plain text for testing
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }
}