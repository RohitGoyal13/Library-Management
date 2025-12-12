package com.rohit.library.controller

import com.rohit.library.model.Book
import com.rohit.library.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(
    private val bookService: BookService
) {

    @PostMapping
    fun addBook(@RequestBody book: Book): ResponseEntity<Book> =
        ResponseEntity.ok(bookService.addBook(book))

    @GetMapping
    fun getAll(): ResponseEntity<List<Book>> =
        ResponseEntity.ok(bookService.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<Book> =
        ResponseEntity.ok(bookService.getById(id))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Unit> {
        bookService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody book: Book): ResponseEntity<Book> =
        ResponseEntity.ok(bookService.update(id, book))
}
