package com.fan.db.repository

import com.fan.db.entity.Notice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface NoticeRepository : JpaRepository<Notice, Long> {


    fun findByCode(code: String): List<Notice>?

    fun findByStatus(status: String): List<Notice>
}
