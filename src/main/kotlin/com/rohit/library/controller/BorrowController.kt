package com.rohit.library.controller

import com.rohit.library.model.BorrowRecord
import com.rohit.library.service.BorrowService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.Authentication

@RestController
@RequestMapping("/borrow")
class BorrowController(
    private val borrowService: BorrowService
) {

    // USER BORROWS A BOOK
    @PostMapping("/{bookId}")
    fun borrowBook(
        auth: Authentication,
        @PathVariable bookId: String
    ): ResponseEntity<BorrowRecord> {

        val username = auth.name
        val result = borrowService.borrowBook(username, bookId)
        return ResponseEntity.ok(result)
    }

    // USER RETURNS A BOOK
    @PostMapping("/return/{bookId}")
    fun returnBook(
        auth: Authentication,
        @PathVariable bookId: String
    ): ResponseEntity<BorrowRecord> {

        val username = auth.name
        val result = borrowService.returnBook(username, bookId)
        return ResponseEntity.ok(result)
    }

    // LIST ALL BORROWED BOOKS OF USER
    @GetMapping("/my")
    fun myBorrowedBooks(auth: Authentication): ResponseEntity<List<BorrowRecord>> {

        val username = auth.name
        val books = borrowService.getMyBorrowedBooks(username)
        return ResponseEntity.ok(books)
    }
}
