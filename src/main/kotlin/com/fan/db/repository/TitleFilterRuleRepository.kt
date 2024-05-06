package com.fan.db.repository

import com.fan.db.entity.TitleFilterRule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface TitleFilterRuleRepository : JpaRepository<TitleFilterRule, Long> {

    fun findAllByKeyword(keyword: String): List<TitleFilterRule>

    fun findByTypeIs(type: String, pageable: Pageable): Page<TitleFilterRule>
    fun findAllByTypeIs(type: String): List<TitleFilterRule>
}



