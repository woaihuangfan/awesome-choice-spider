package com.fan.db.repository

import com.fan.db.entity.NoticeDetailFailLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeDetailFailLogRepository : JpaRepository<NoticeDetailFailLog, Long> {
    fun countByYear(year: String): Int

    fun findByCodeAndStock(
        code: String,
        stock: String,
    ): NoticeDetailFailLog?

    fun deleteByCodeAndStock(
        code: String,
        stock: String,
    )
}
