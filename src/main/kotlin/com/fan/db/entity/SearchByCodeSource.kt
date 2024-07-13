package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity(name = "search_by_code_source")
@Table(
    name = "search_by_code_source",
    indexes = [
        Index(name = "idx_year", columnList = "year", unique = true),
        Index(name = "idx_stock", columnList = "stock", unique = true),
        Index(name = "idx_request_id", columnList = "request_id", unique = true),
    ],
)
class SearchByCodeSource(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
)
