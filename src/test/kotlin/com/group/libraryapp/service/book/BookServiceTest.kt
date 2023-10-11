package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.book.BookType.*
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
        val bookType = COMPUTER
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
        assertThat(loanHistory.user.name).isEqualTo(userName)
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
        assertThat(loanHistory.user.name).isEqualTo(userName)
        assertThat(loanHistory.status).isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    @DisplayName("countLoanedBook: 정상 케이스")
    fun countLoanedBook() {
        // given
        val savedUser = userRepository.save(User(name = "a", age = null))
        val books = bookRepository.saveAll(
            listOf(
                Book.fixture(name = "책1"),
                Book.fixture(name = "책2"),
                Book.fixture(name = "책3"),
            )
        )
        books.forEach(savedUser::loanBook)

        val returnCount = 2
        for (i in 0 until returnCount) {
            savedUser.returnBook(books[i].name)
        }

        // when
        val loanedBookCount = bookService.countLoanedBook()

        // then
        assertThat(loanedBookCount).isEqualTo(books.size - returnCount)
    }

    @Test
    @DisplayName("getBookStatistics: 정상 케이스")
    fun getBookStatistics() {
        // given
        val books = mapOf(
            COMPUTER to listOf(
                Book.fixture(name = "COMPUTER 책1", type = COMPUTER),
                Book.fixture(name = "COMPUTER 책2", type = COMPUTER),
                Book.fixture(name = "COMPUTER 책3", type = COMPUTER),
            ),
            SCIENCE to listOf(
                Book.fixture(name = "SCIENCE 책1", type = SCIENCE),
                Book.fixture(name = "SCIENCE 책2", type = SCIENCE),
            ),
            SOCIETY to listOf(
                Book.fixture(name = "SOCIETY 책1", type = SOCIETY),
            ),
            ECONOMY to listOf(
                Book.fixture(name = "ECONOMY 책1", type = ECONOMY),
            ),
            LANGUAGE to listOf(
                Book.fixture(name = "LANGUAGE 책1", type = LANGUAGE),
                Book.fixture(name = "LANGUAGE 책2", type = LANGUAGE),
                Book.fixture(name = "LANGUAGE 책3", type = LANGUAGE),
                Book.fixture(name = "LANGUAGE 책4", type = LANGUAGE),
            ),
        )

        bookRepository.saveAll(books.flatMap { (_, books) -> books })

        // when
        val bookStatistics = bookService.getBookStatistics()

        // then
        assertThat(bookStatistics).hasSize(books.size)

        for (type in books.keys) {
            val bookStatistic = bookStatistics.first { result -> result.type == type }
            assertThat(bookStatistic.count).isEqualTo(books[type]!!.size.toLong())
        }
    }

}