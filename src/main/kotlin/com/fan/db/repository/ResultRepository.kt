package com.fan.db.repository

import com.fan.db.entity.Result
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ResultRepository : JpaRepository<Result, Long> {
    fun findByStockAndCode(stock: String, code: String): Result?
    fun countByYear(year: String): Int
}
