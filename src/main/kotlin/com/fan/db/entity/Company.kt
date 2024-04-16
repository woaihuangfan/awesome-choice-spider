package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity(name = "company")
@Table(
    name = "company",
    indexes = [
        Index(name = "idx_stock", columnList = "stock", unique = true),
    ]
)
class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    @Column(name = "stock", nullable = false, unique = true)
    val stock: String,

    @Column(name = "company_name", nullable = true)
    var companyName: String? = "",
//    @Column(name = "market_type", nullable = false, unique = true)
//    val marketType: String
) {

}