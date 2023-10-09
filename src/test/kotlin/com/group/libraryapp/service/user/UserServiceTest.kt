package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
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

}