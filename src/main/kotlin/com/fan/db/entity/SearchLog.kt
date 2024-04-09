package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "search_log")
class SearchLog(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    @Column(name = "count", nullable = false)
    val count: Int,
    @Column(name = "date", nullable = false)
    var date: String,
    @Column(name = "param", nullable = false)
    var param: String,
    @Column(name = "type", nullable = false)
    var type: String,

    )