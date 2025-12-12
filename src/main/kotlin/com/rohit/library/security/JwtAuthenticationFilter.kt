package com.rohit.library.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            try {
                val username = jwtUtil.extractUsername(token)
                val role = jwtUtil.extractRole(token)   // <-- NEW

                if (SecurityContextHolder.getContext().authentication == null) {

                    val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

                    val authToken = UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                    )

                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }

            } catch (e: Exception) {
                println("JWT ERROR: ${e.message}")
            }
        }

        filterChain.doFilter(request, response)
    }
}
