package com.group.libraryapp.service.user

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import javax.transaction.Transactional

@Transactional
@SpringBootTest
open class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val bookRepository: BookRepository,
) {

    @Test
    @DisplayName("saveUser: 정상 케이스 테스트")
    fun saveUser() {
        // given
        val name = "이정주"
        val age = 999
        val request = UserCreateRequest(name, age)

        // when
        userService.saveUser(request)
        val results = userRepository.findAll()

        // then
        assertThat(results).hasSize(1)

        val user = results[0]
        assertThat(user.name).isEqualTo(request.name)
        assertThat(user.age).isEqualTo(request.age)
    }

    @Test
    @DisplayName("getUsers: 정상 케이스 테스트")
    fun getUsers() {
        // given
        val name1 = "A"
        val age1 = 20

        val name2 = "B"
        val age2 = null
        userRepository.saveAll(
            listOf(
                User(name = name1, age = age1),
                User(name = name2, age = age2),
            )
        )

        // when
        val results = userService.getUsers()

        // then
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name").containsExactlyInAnyOrder(name1, name2)
        assertThat(results).extracting("age").containsExactlyInAnyOrder(age1, age2)
    }

    @Test
    @DisplayName("updateUserName: 정상 케이스 테스트")
    fun updateUserName() {
        // given
        val savedUser = userRepository.save(User(name = "A", age = null))

        val newName = "B"
        val request = UserUpdateRequest(savedUser.id!!, newName)

        // when
        userService.updateUserName(request)
        val updatedUser = userRepository.findByIdOrNull(savedUser.id!!)

        // then
        requireNotNull(updatedUser)
        assertThat(updatedUser.name).isEqualTo(newName)
    }

    @Test
    @DisplayName("getUserLoanHistories: 정상 케이스")
    fun getUserLoanHistories() {
        // given
        val user = userRepository.save(User(name = "user1", age = null))

        // when
        val userLoanHistories = userService.getUserLoanHistories()

        // then
        assertThat(userLoanHistories).hasSize(1)

        val (userLoanHistory) = userLoanHistories
        assertThat(userLoanHistory.name).isEqualTo(user.name)
        assertThat(userLoanHistory.books).isEmpty()
    }

    @Test
    @DisplayName("getUserLoanHistories: 대출 기록 다수 유저 케이스")
    fun getUserLoanHistories2() {
        // given
        val user = userRepository.save(User(name = "user1", age = null))
        val books = listOf(
            Book.fixture(name = "책1"),
            Book.fixture(name = "책2"),
            Book.fixture(name = "책3"),
        )
        bookRepository.saveAll(books)
        books.forEach(user::loanBook)

        user.returnBook(books[0].name)

        // when
        val userLoanHistories = userService.getUserLoanHistories()

        // then
        assertThat(userLoanHistories).hasSize(1)

        val (userLoanHistory) = userLoanHistories
        assertThat(userLoanHistory.name).isEqualTo(user.name)
        assertThat(userLoanHistory.books).hasSize(3)
        assertThat(userLoanHistory.books).extracting("name")
            .containsExactlyInAnyOrder(*user.userLoanHistories.map { it.bookName }.toTypedArray())
        assertThat(userLoanHistory.books).extracting("isReturn")
            .containsExactlyInAnyOrder(*user.userLoanHistories.map { it.status == UserLoanStatus.RETURNED }.toTypedArray())
    }

}