package com.fan.db.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ResultRepository : JpaRepository<com.fan.db.entity.Result, Long> {

}
