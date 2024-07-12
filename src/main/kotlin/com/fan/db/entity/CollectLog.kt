package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "collect_log")
class CollectLog(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    @Column(name = "count", nullable = false)
    val count: Int,
    @Column(name = "date", nullable = false)
    var date: String,
    @Column(name = "till_date", nullable = false)
    var tillDate: String,
    @Column(name = "keyword", nullable = false)
    var keyword: String,
    @Column(name = "title_filtered_words", columnDefinition = "VARCHAR(8192) not null default ''")
    var titleFilteredWords: String,
    @Column(name = "type", nullable = false)
    var type: String,
    @Column(name = "requestId", columnDefinition = "VARCHAR(8192) not null default ''")
    var requestId: String,
)
