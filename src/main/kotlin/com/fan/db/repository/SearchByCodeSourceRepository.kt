package com.fan.db.repository

import com.fan.db.entity.SearchByCodeSource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface SearchByCodeSourceRepository : JpaRepository<SearchByCodeSource, Long>, CountByRequestIdRepository {
    override fun countByRequestId(requestId: String): Int


    fun findByCodeAndTitleAndDate(code: String, title: String, date: String): SearchByCodeSource?
}