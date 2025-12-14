package com.rohit.library

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled("Disabled because gRPC server starts during context load")
class LibraryManagementApplicationTests {

    @Test
    fun contextLoads() {
    }
}
