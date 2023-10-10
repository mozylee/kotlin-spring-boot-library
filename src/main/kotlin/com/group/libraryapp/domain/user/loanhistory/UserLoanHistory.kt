package com.group.libraryapp.domain.user.loanhistory

import com.group.libraryapp.domain.user.User
import javax.persistence.*

@Entity
class UserLoanHistory constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    val user: User?,

    val bookName: String?,

    @Enumerated(EnumType.STRING)
    var status: UserLoanStatus = UserLoanStatus.LOANED,
) {

    fun doReturn() {
        this.status = UserLoanStatus.RETURNED
    }

}