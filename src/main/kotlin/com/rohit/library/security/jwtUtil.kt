package com.rohit.library.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {

    // NOTE: for production, load this from config and keep it secret (env / vault).
    private val secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256)

    fun generateToken(username: String): String {
        val now = Date()
        val expiry = Date(now.time + 1000L * 60 * 60) // 1 hour
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey)
            .compact()
    }

    fun extractUsername(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject
    }
}
