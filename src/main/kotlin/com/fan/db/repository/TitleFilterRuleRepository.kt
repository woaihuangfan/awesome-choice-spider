package com.fan.db.repository

import com.fan.db.entity.TitleFilterRule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface TitleFilterRuleRepository : JpaRepository<TitleFilterRule, Long> {

    fun findAllByKeyword(keyword: String): List<TitleFilterRule>

}



