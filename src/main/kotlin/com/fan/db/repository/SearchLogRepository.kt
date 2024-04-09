package com.fan.db.repository

import com.fan.db.entity.SearchLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface SearchLogRepository : JpaRepository<SearchLog, Long> {
}
