package com.fan.db.repository

import com.fan.db.entity.Blog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface BlogRepository : JpaRepository<Blog, Long> {

}
