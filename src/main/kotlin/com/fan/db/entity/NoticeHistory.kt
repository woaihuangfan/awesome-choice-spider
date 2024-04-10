package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "notice_history")
class NoticeHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    @Column(name = "code", nullable = false)
    val code: String,
    @Column(name = "count", nullable = false)
    val count: Int,
    @Column(name = "date", nullable = false)
    var date: String,
    @Column(name = "year", nullable = false)
    var year: Int,
    @Column(name = "status", nullable = false)
    var status: String,

    )