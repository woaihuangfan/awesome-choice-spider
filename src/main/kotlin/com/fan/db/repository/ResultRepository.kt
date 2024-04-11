package com.fan.db.repository

import com.fan.db.entity.Result
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ResultRepository : JpaRepository<Result, Long> {
    fun findByStockAndYearAndCode(stock: String, year: String, code: String): Result?
}
