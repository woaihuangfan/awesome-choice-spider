package com.fan.db.repository

import com.fan.db.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface CodeRepository : JpaRepository<Company, Long> {

    fun findByStock(stock: String): Company?
}
