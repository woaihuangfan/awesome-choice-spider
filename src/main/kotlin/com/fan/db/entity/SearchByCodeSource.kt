package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "search_by_code_source")
class SearchByCodeSource(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "code", nullable = false)
    val code: String,

    @Column(name = "stock", nullable = false)
    val stock: String,
    @Column(name = "columnCode", nullable = false)
    var columnCode: String,
    @Column(name = "columnName", nullable = false)
    var columnName: String,

    @Column(name = "title", nullable = false, length = 8196)
    var title: String,

    @Column(name = "date", nullable = false)
    var date: String,
    @Column(name = "`year`", nullable = false)
    var year: String,
    @Column(name = "request_id", nullable = false)
    var requestId: String,

    @Column(name = "company_name", nullable = false)
    var companyName: String,

    @Column(name = "create_date", nullable = false)
    var createDate: String,
) {

}