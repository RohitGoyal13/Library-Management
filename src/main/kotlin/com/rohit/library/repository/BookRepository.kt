package com.rohit.library.repository

import com.rohit.library.model.Book
import org.springframework.data.mongodb.repository.MongoRepository

interface BookRepository : MongoRepository<Book, String>
