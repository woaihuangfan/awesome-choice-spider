package com.fan.db.repository

import com.fan.db.entity.NoticeSearchHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface NoticeSearchHistoryRepository : JpaRepository<NoticeSearchHistory, Long> {

    fun findByStockAndYear(stock: String, year: String): NoticeSearchHistory?
}
