package com.fan.db.repository

import com.fan.db.entity.CompanyFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface CompanyFileRepository : JpaRepository<CompanyFile, Long> {
}



