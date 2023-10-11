package com.group.libraryapp.domain.user.loanhistory

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus.LOANED
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus.RETURNED
import javax.persistence.*

@Entity
class UserLoanHistory constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    val user: User,

    val bookName: String,

    @Enumerated(EnumType.STRING)
    var status: UserLoanStatus = LOANED,
) {

    val isReturn: Boolean
        get() = this.status == RETURNED

    fun doReturn() {
        this.status = RETURNED
    }

    companion object {
        fun fixture(
            user: User,
            bookName: String = "책 제목",
        ) = UserLoanHistory(
            user = user,
            bookName = bookName,
            status = LOANED,
        )
    }

}