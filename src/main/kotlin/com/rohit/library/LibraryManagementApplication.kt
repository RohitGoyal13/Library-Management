package com.rohit.library

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
    exclude = [UserDetailsServiceAutoConfiguration::class]
)
class LibraryManagementApplication

fun main(args: Array<String>) {
    runApplication<LibraryManagementApplication>(*args)
}
