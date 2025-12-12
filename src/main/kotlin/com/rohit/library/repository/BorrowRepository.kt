package com.rohit.library.repository

import com.rohit.library.model.BorrowRecord
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BorrowRepository : MongoRepository<BorrowRecord, String> {

    fun findByUserId(userId: String): List<BorrowRecord>

    fun findByUserIdAndBookIdAndReturnedFalse(userId: String, bookId: String): BorrowRecord?

    fun findByExpiresAtBeforeAndReturnedFalse(time: LocalDateTime): List<BorrowRecord>

    fun findByPolicyReturnAtBeforeAndReturnedFalse(time: LocalDateTime): List<BorrowRecord>
}
