package com.fan.db.repository

import com.fan.db.entity.Source
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface SourceRepository : JpaRepository<Source, Long>, CountByRequestIdRepository {
    override fun countByRequestId(requestId: String): Int
}
