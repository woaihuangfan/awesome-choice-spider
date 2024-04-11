package com.fan.db.entity

import com.fan.event.NoticeDetailSaveEvent
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import org.springframework.data.domain.DomainEvents

@Entity(name = "notice_detail")
class NoticeDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "notice_id", nullable = false)
    val noticeId: Long,

    @Column(name = "code", nullable = false)
    val code: String,

    @Column(name = "title", nullable = false, length = 8196)
    val title: String,

    @Lob
    @Column(name = "content", nullable = false)
    val content: String,

    @Column(name = "stock", nullable = false)
    val stock: String,

    ) {


    @DomainEvents
    fun domainEvents(): Collection<NoticeDetailSaveEvent> {
        return listOf(NoticeDetailSaveEvent(this))
    }
}