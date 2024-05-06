package com.fan.db.repository

import com.fan.db.entity.CollectLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface CollectLogRepository : JpaRepository<CollectLog, Long> {
    fun findAllByTillDateIs(tillDate: String): List<CollectLog>
    fun findTop1ByOrderByIdDesc(): CollectLog
}
