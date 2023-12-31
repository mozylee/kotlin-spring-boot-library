package com.group.libraryapp.controller.book

import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.response.UserResponse
import com.group.libraryapp.service.book.BookService
import com.group.libraryapp.service.user.UserService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/book")
@RestController
class BookController(
    private val bookService: BookService,
) {

    @PostMapping
    fun saveBook(@RequestBody request: BookRequest) = bookService.saveBook(request)

    @PostMapping("/loan")
    fun loanBook(@RequestBody request: BookLoanRequest) = bookService.loanBook(request)

    @GetMapping("/loan")
    fun countLoanedBook() = bookService.countLoanedBook()

    @GetMapping("/stat")
    fun getBookStatistics() = bookService.getBookStatistics()

    @PutMapping("/return")
    fun returnBook(@RequestBody request: BookReturnRequest) = bookService.returnBook(request)

}