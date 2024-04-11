package com.fan.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "analysis_log")
class AnalysisLog(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "new", nullable = false)
    val new: Int,

    @Column(name = "valid_titles", nullable = false)
    val validTitles: Int,

    @Column(name = "valid_details", nullable = false)
    val validDetails: Int,

    @Column(name = "success_accounts", nullable = false)
    val successAccounts: Int,

    @Column(name = "failed_accounts", nullable = false)
    val failedAccounts: Int,


    @Column(name = "type", nullable = false)
    val type: String,

    @Column(name = "date", nullable = false, length = 8196)
    val date: String,

    ) {

}