package com.rohit.library.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import java.util.Base64

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secret: String
) {

    private val secretKey: SecretKey =
        Keys.hmacShaKeyFor(
            Base64.getEncoder().encode(secret.toByteArray())
        )

    fun generateToken(username: String, role: String): String {
        val now = Date()
        val expiry = Date(now.time + 1000L * 60 * 60)

        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUsername(token: String): String =
        extractAllClaims(token).subject

    fun extractRole(token: String): String =
        extractAllClaims(token).get("role", String::class.java)

    private fun extractAllClaims(token: String) =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
}
