package com.fan.db.repository

import com.fan.db.entity.Result
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ResultRepository : JpaRepository<Result, Long> {

}
