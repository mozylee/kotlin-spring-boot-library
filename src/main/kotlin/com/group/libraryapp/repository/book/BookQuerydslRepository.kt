package com.group.libraryapp.repository.book

import com.group.libraryapp.domain.book.QBook.book
import com.group.libraryapp.dto.book.response.BookStatisticResponse
import com.querydsl.core.types.Projections
import com.querydsl.jpa.hibernate.HibernateQueryFactory
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class BookQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun getStatistics(): List<BookStatisticResponse> = queryFactory
        .select(
            Projections.constructor(
                BookStatisticResponse::class.java,
                book.type,
                book.id.count()
            )
        )
        .from(book)
        .groupBy(book.type)
        .fetch()

}