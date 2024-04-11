package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob

@Entity(name = "notice_detail_fail_log")
class NoticeDetailFailLog(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

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

    @Column(name = "`year`", nullable = false)
    val year: String
) {

}