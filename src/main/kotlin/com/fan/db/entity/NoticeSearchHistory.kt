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
    var count: Int,
    @Column(name = "`year`", nullable = false)
    var year: String,

    @Column(name = "last_update_date", nullable = false)
    var lastUpdatedDate: String,

    )