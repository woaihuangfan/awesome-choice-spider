package com.fan.db.repository

import com.fan.db.entity.AnalysisLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalysisLogRepository : JpaRepository<AnalysisLog, Long>
