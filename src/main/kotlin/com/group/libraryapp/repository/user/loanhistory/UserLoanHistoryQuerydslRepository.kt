package com.group.libraryapp.repository.user.loanhistory

import com.group.libraryapp.domain.user.loanhistory.QUserLoanHistory.userLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.querydsl.jpa.hibernate.HibernateQueryFactory
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class UserLoanHistoryQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) {

    // default parameter와 scope function을 활용하여 가변적인 조건 처리 가능
    fun find(
        bookName: String,
        status: UserLoanStatus? = null,
    ): UserLoanHistory? = queryFactory
        .select(userLoanHistory)
        .from(userLoanHistory)
        .where(
            userLoanHistory.bookName.eq(bookName),
            status?.let { userLoanHistory.status.eq(status) }
        )
        .limit(1)
        .fetchOne()

    fun count(status: UserLoanStatus): Long = queryFactory
        .select(userLoanHistory.count())
        .from(userLoanHistory)
        .where(
            userLoanHistory.status.eq(status)
        )
        .fetchOne() ?: 0L

}