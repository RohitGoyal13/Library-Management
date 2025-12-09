package com.rohit.library.service

import com.rohit.library.dto.AuthRequest
import com.rohit.library.dto.AuthResponse
import com.rohit.library.model.User
import com.rohit.library.repository.UserRepository
import com.rohit.library.security.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    fun signup(request: AuthRequest): AuthResponse {
        if (userRepository.findByUsername(request.username) != null) {
            throw IllegalArgumentException("User already exists")
        }
        val hashed = passwordEncoder.encode(request.password)
        val saved = userRepository.save(User(username = request.username, password = hashed))
        val token = jwtUtil.generateToken(saved.username)
        return AuthResponse(token)
    }

    fun login(request: AuthRequest): AuthResponse {
        val user = userRepository.findByUsername(request.username) ?: throw IllegalArgumentException("Invalid credentials")
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }
        val token = jwtUtil.generateToken(user.username)
        return AuthResponse(token)
    }
}
