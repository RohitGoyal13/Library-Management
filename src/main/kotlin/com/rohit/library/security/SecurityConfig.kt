package com.rohit.library.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtAuthenticationFilter
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }  // 🔥 IMPORTANT

            .authorizeHttpRequests {
            it.requestMatchers("/auth/**", "/actuator/**").permitAll()

            it.requestMatchers("/borrow/return/**").hasRole("USER")

            it.requestMatchers("/books/**").hasRole("ADMIN")
            it.requestMatchers("/borrow/**").hasRole("USER")
            it.anyRequest().authenticated()
        }


            .httpBasic { it.disable() }
            .formLogin { it.disable() }

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
