package com.fan.db.repository

import com.fan.db.entity.Code
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface CodeRepository : JpaRepository<Code, Long> {

    fun findByCode(code: String): Code?
}
