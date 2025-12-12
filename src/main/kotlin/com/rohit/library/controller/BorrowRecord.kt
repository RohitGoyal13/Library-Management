package com.rohit.library.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("borrow_records")
data class BorrowRecord(
    @Id val id: String? = null,
    val userId: String,
    val bookId: String,
    val borrowedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,    // nullable → optional expiry
    val policyReturnAt: LocalDateTime? = null, // for 10 PM auto-return policy
    var returned: Boolean = false,
    var returnedAt: LocalDateTime? = null
)
