package com.fan.db.repository

import com.fan.db.entity.NoticeDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface NoticeDetailRepository : JpaRepository<NoticeDetail, Long> {
    fun findByCode(code: String): NoticeDetail
}
