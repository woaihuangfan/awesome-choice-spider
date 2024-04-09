package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob

@Entity(name = "source")
class Source(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "code", nullable = false)
    val code: String,
    @Column(name = "columnCode", nullable = false)
    var columnCode: String,
    @Column(name = "title", nullable = false)
    var title: String,
    @Lob
    @Column(name = "content", nullable = false)
    var content: String,
    @Column(name = "date", nullable = false)
    var date: String,
    @Column(name = "securityFullName", nullable = false)
    var securityFullName: String,
    @Column(name = "url", nullable = false)
    var url: String,
    @Column(name = "status", nullable = true)
    var status: String? = "",
    @Column(name = "request_id", nullable = false)
    var requestId: String

) {

}