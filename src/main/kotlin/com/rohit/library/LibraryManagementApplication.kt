package com.rohit.library

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import // <--- NEW
import com.rohit.library.security.SecurityConfig   // <--- CRITICAL LINE

@SpringBootApplication
@Import(SecurityConfig::class) // <--- WE ARE FORCING IT HERE
class LibraryManagementApplication

fun main(args: Array<String>) {
    runApplication<LibraryManagementApplication>(*args)
}