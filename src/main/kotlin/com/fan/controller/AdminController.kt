package com.fan.controller

import com.fan.service.WebDataCollector
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException


@RestController
@RequestMapping("/admin")
class AdminController(private val webDataCollector: WebDataCollector) {

    @GetMapping(value = ["/start"])
    fun start(): String {
        try {
            val keywords = listOf("续聘", "会计", "审计", "聘用", "2024")
            keywords.forEach { keyword ->
                webDataCollector.start(keyword)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "success"
    }
}
