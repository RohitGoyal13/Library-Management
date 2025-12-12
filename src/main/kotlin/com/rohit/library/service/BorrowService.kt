package com.rohit.library.service

import com.rohit.library.model.BorrowRecord
import com.rohit.library.repository.BookRepository
import com.rohit.library.repository.BorrowRepository
import com.rohit.library.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BorrowService(
    private val bookRepository: BookRepository,
    private val borrowRepository: BorrowRepository,
    private val userRepository: UserRepository
) {

    // USER BORROWS A BOOK
    fun borrowBook(username: String, bookId: String): BorrowRecord {

        val user = userRepository.findByUsername(username)
            ?: throw RuntimeException("User not found")

        val book = bookRepository.findById(bookId)
            .orElseThrow { RuntimeException("Book not found") }

        if (book.quantity <= 0) {
            throw RuntimeException("Book not available")
        }

        // Check if user already borrowed this book
        val existingBorrow = borrowRepository
            .findByUserIdAndBookIdAndReturnedFalse(user.id!!, bookId)

        if (existingBorrow != null) {
            throw RuntimeException("You already borrowed this book")
        }

        // Decrease stock
        bookRepository.save(book.copy(quantity = book.quantity - 1))

        // Future additions:
        // val expiresAt = LocalDateTime.now().plusDays(7)
        // val policyReturnAt = LocalDateTime.now().withHour(22).withMinute(0)

        val borrowRecord = BorrowRecord(
            userId = user.id!!,
            bookId = bookId,
            borrowedAt = LocalDateTime.now(),
            expiresAt = null,        // will add later
            policyReturnAt = null,   // will add later
            returned = false
        )

        return borrowRepository.save(borrowRecord)
    }


    // USER RETURNS A BOOK
    fun returnBook(username: String, bookId: String): BorrowRecord {

        val user = userRepository.findByUsername(username)
            ?: throw RuntimeException("User not found")

        val record = borrowRepository
            .findByUserIdAndBookIdAndReturnedFalse(user.id!!, bookId)
            ?: throw RuntimeException("You haven't borrowed this book")

        record.returned = true
        record.returnedAt = LocalDateTime.now()

        // increase stock back
        val book = bookRepository.findById(bookId)
            .orElseThrow { RuntimeException("Book not found") }

        bookRepository.save(book.copy(quantity = book.quantity + 1))

        return borrowRepository.save(record)
    }


    // LIST USER'S BORROWED BOOKS
    fun getMyBorrowedBooks(username: String): List<BorrowRecord> {

        val user = userRepository.findByUsername(username)
            ?: throw RuntimeException("User not found")

        return borrowRepository.findByUserId(user.id!!)
    }
}
