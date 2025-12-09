package com.rohit.library.controller

import com.rohit.library.dto.AuthRequest
import com.rohit.library.dto.AuthResponse
import com.rohit.library.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/signup")
    fun signup(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val resp = authService.signup(request)
        return ResponseEntity.ok(resp)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val resp = authService.login(request)
        return ResponseEntity.ok(resp)
    }
}
