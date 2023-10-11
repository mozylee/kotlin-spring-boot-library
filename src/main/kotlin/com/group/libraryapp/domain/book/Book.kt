package com.group.libraryapp.domain.book

import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Entity
class Book constructor(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null,
    val name: String,
    @Enumerated(EnumType.STRING)
    val type: BookType,
) {

    init {
        require(name.isNotBlank()) { "이름은 비어 있을 수 없습니다" }
    }

    companion object {
        // 생성자 대신, 정적 팩토리 메서드를 활용하여 새로운 필드가 생겼을 경우 발생하는 에러 위치를 이곳으로 한정시킴
        fun fixture(
            name: String = "책 이름",
            type: BookType = BookType.COMPUTER,
            id: Long? = null,
        ): Book = Book(
            id = id,
            name = name,
            type = type,
        )
    }

}