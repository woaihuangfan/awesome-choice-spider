package com.fan.db.entity

import com.fan.event.ResultSaveEvent
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.domain.DomainEvents

@Entity(name = "result")
class Result(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "notice_id", nullable = false)
    val noticeId: Long,

    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "stock", nullable = false)
    val stock: String,
    @Column(name = "date", nullable = false)
    val date: String,
    @Column(name = "accountCompanyName", nullable = false)
    val accountCompanyName: String,
    @Column(name = "code", nullable = false, unique = true)
    val code: String,
    @Column(name = "amount", nullable = true)
    val amount: String? = ""
) {

    @DomainEvents
    fun domainEvents(): Collection<ResultSaveEvent> {
        return listOf(ResultSaveEvent(this));
    }
}