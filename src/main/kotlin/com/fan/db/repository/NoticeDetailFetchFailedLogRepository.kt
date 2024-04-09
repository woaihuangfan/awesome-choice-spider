package com.fan.db.repository

import com.fan.db.entity.NoticeDetailFetchFailedLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface NoticeDetailFetchFailedLogRepository : JpaRepository<NoticeDetailFetchFailedLog, Long> {

}
