package com.rohit.library.service

import com.rohit.library.model.Book
import com.rohit.library.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository
) {

    fun addBook(book: Book): Book = bookRepository.save(book)

    fun getAll(): List<Book> = bookRepository.findAll()

    fun getById(id: String): Book =
        bookRepository.findById(id).orElseThrow { RuntimeException("Book not found") }

    fun delete(id: String) = bookRepository.deleteById(id)

    fun update(id: String, updated: Book): Book {
        val existing = getById(id)
        val newBook = existing.copy(
            title = updated.title,
            author = updated.author,
            quantity = updated.quantity
        )
        return bookRepository.save(newBook)
    }
}
