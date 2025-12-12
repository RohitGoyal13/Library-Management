package com.rohit.library.scheduler

import com.rohit.library.repository.BorrowRepository
import com.rohit.library.repository.BookRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AutoReturnScheduler(
    private val borrowRepository: BorrowRepository,
    private val bookRepository: BookRepository
) {

    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    fun autoReturnBooks() {

        val now = LocalDateTime.now()

        // 1️⃣ Expired books
        val expired = borrowRepository
            .findByExpiresAtBeforeAndReturnedFalse(now)

        expired.forEach { record ->
            record.returned = true
            record.returnedAt = now

            // increase stock
            val book = bookRepository.findById(record.bookId).orElse(null)
            if (book != null) {
                bookRepository.save(book.copy(quantity = book.quantity + 1))
            }

            borrowRepository.save(record)
        }

        // 2️⃣ Policy 10PM returns
        val policyReturns = borrowRepository
            .findByPolicyReturnAtBeforeAndReturnedFalse(now)

        policyReturns.forEach { record ->
            record.returned = true
            record.returnedAt = now

            val book = bookRepository.findById(record.bookId).orElse(null)
            if (book != null) {
                bookRepository.save(book.copy(quantity = book.quantity + 1))
            }

            borrowRepository.save(record)
        }
    }
}
