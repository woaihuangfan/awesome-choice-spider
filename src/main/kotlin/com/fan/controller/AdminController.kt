package com.fan.controller

import com.fan.enums.SearchType
import com.fan.service.SearchByCodeCollector
import com.fan.service.SearchKeywordDataCollector
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException


@RestController
@RequestMapping("/admin")
class AdminController(
    searchKeywordDataCollector: SearchKeywordDataCollector,
    searchByCodeCollector: SearchByCodeCollector
) {
    private val collectorMap = mapOf(
        SearchType.KEYWORD.typeName to searchKeywordDataCollector,
        SearchType.CODE.typeName to searchByCodeCollector
    )

    @GetMapping(value = ["/start"])
    fun start(
        @RequestParam param: String,
        @RequestParam type: SearchType,
    ): String {
        try {
            if (collectorMap.containsKey(type.typeName)) {
                collectorMap[type.typeName]!!.start(param, type)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "success"
    }
}
