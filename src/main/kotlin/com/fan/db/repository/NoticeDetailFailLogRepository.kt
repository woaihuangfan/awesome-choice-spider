package com.fan.db.repository

import com.fan.db.entity.NoticeDetailFailLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface NoticeDetailFailLogRepository : JpaRepository<NoticeDetailFailLog, Long> {

    fun findAllByYear(year: String): List<NoticeDetailFailLog>

    fun findByCode(code: String): NoticeDetailFailLog?

    fun deleteByCode(code: String)
}
