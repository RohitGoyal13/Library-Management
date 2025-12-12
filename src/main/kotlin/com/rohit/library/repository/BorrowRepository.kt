package com.rohit.library.repository

import com.rohit.library.model.BorrowRecord
import org.springframework.data.mongodb.repository.MongoRepository

interface BorrowRepository : MongoRepository<BorrowRecord, String> {

    fun findByUserId(userId: String): List<BorrowRecord>

    fun findByUserIdAndBookIdAndReturnedFalse(
        userId: String,
        bookId: String
    ): BorrowRecord?
}

