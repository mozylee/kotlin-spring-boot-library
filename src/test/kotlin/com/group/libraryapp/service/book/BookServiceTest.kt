package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@Transactional
@SpringBootTest
open class BookServiceTest @Autowired constructor(
    private val bookService: BookService,

    private val bookRepository: BookRepository,

    private val userRepository: UserRepository,

    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @Test
    @DisplayName("saveBook: 정상 케이스")
    fun saveBook() {
        // given
        val bookName = "이상한 나라의 앨리스"
        val bookType = BookType.COMPUTER
        val request = BookRequest(bookName, bookType)

        // when
        bookService.saveBook(request)
        val books = bookRepository.findAll()

        // then
        assertThat(books).hasSize(1)

        val book = books[0]
        assertThat(book.name).isEqualTo(bookName)
        assertThat(book.type).isEqualTo(bookType)
    }

    @Test
    @DisplayName("loanBook: 1. 정상 케이스")
    fun loanBook() {
        // given
        val bookName = "이상한 나라의 엘리스"
        val userName = "이정주"

        bookRepository.save(Book.fixture(name = bookName))
        userRepository.save(User(name = userName, age = 999))
        val request = BookLoanRequest(userName, bookName)

        // when
        bookService.loanBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)

        val (loanHistory) = results
        assertThat(loanHistory.bookName).isEqualTo(bookName)
        assertThat(loanHistory.user!!.name).isEqualTo(userName)
        assertThat(loanHistory.status).isEqualTo(UserLoanStatus.LOANED)
    }

    @Test
    @DisplayName("loanBook: 2. 실패 케이스 - 이미 대출되어 있다면 실패")
    fun loanBook_fail() {
        // given
        val bookName = "이상한 나라의 엘리스"
        val userName = "이정주"

        bookRepository.save(Book.fixture(name = bookName))
        userRepository.save(User(name = userName, age = 999))
        val firstRequest = BookLoanRequest(userName, bookName)
        val secondRequest = BookLoanRequest("김정주", bookName)

        // when
        bookService.loanBook(firstRequest)
        val message = assertThrows<IllegalArgumentException> { bookService.loanBook(secondRequest) }.message

        // then
        assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
    }

    @Test
    @DisplayName("returnBook: 정상 케이스")
    fun returnBook() {
        // given
        val bookName = "이상한 나라의 엘리스"
        val userName = "이정주"

        bookRepository.save(Book.fixture(name = bookName))
        userRepository.save(User(name = userName, age = 999))
        val loanRequest = BookLoanRequest(userName, bookName)
        val returnRequest = BookReturnRequest(userName, bookName)

        // when
        bookService.loanBook(loanRequest)
        bookService.returnBook(returnRequest)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)

        val (loanHistory) = results
        assertThat(loanHistory.bookName).isEqualTo(bookName)
        assertThat(loanHistory.user?.name).isEqualTo(userName)
        assertThat(loanHistory.status).isEqualTo(UserLoanStatus.RETURNED)
    }

}