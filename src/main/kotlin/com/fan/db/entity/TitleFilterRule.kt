package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "title_filter_rules")
class TitleFilterRule(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    @Column(name = "keyword", nullable = false)
    val keyword: String,
    @Column(name = "type", columnDefinition = "VARCHAR(32) not null default ''")
    val type: String,
)
