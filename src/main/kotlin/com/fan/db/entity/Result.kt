package com.fan.db.entity

import com.fan.event.ResultSaveEvent
import com.fan.service.RequestContext
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.domain.DomainEvents

@Entity(name = "result")
@Table(
    name = "result",
    indexes = [
        Index(name = "idx_stock", columnList = "stock", unique = true),
        Index(name = "idx_request_id", columnList = "request_id", unique = true),
    ],
)
class Result(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @Column(name = "notice_id", nullable = false)
    val noticeId: Long,
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "stock", nullable = false)
    val stock: String,
    @Column(name = "date", nullable = false)
    val date: String,
    @Column(name = "accountCompanyName", nullable = false)
    var accountCompanyName: String,
    @Column(name = "code", nullable = false)
    val code: String,
    @Column(name = "amount", nullable = true)
    val amount: String? = "",
    @Column(name = "`year`", nullable = false)
    val year: String,
    @Column(name = "title", nullable = false, length = 8196)
    var title: String,
    @Column(name = "request_id", columnDefinition = "VARCHAR(8192) not null default ''")
    var requestId: String,
    @Transient
    val context: RequestContext,
) {
    @DomainEvents
    fun domainEvents(): Collection<ResultSaveEvent> = listOf(ResultSaveEvent(this))
}
