package com.fan.db.entity

import com.fan.event.NoticeSaveEvent
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.domain.DomainEvents

@Entity(name = "notice")
class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(name = "code", nullable = false)
    val code: String,

    @Column(name = "stock", nullable = false)
    val stock: String,
    @Column(name = "columnCode", nullable = false)
    var columnCode: String,
    @Column(name = "title", nullable = false, length = 8196)
    var title: String,
    @Column(name = "date", nullable = false)
    var date: String,
    @Column(name = "securityFullName", nullable = false)
    var securityFullName: String,
    @Column(name = "status", nullable = false)
    var status: String? = "",
    @Column(name = "source", nullable = false)
    var source: String,
    @Column(name = "requestId", nullable = false)
    var requestId: String,
    @Column(name = "`year`", nullable = false)
    val year: String,

) {

    @DomainEvents
    fun domainEvents(): Collection<NoticeSaveEvent> {
        return listOf(NoticeSaveEvent(this));
    }

    fun isDone(): Boolean {
        return this.status.equals("Done", ignoreCase = true)
    }
}