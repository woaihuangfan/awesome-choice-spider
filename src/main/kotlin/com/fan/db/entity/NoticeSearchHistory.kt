package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "notice_search_history")
class NoticeSearchHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    @Column(name = "stock", nullable = false)
    val stock: String,
    @Column(name = "count", nullable = false)
    val count: Int,
    @Column(name = "date", nullable = false)
    val date: String,
    @Column(name = "till_date", nullable = false)
    val tillDate: String,
    @Column(name = "requestId", nullable = false)
    val requestId: String,
)
