package com.fan.controller

import cn.hutool.core.date.DateUtil
import com.fan.enums.SearchType
import com.fan.po.DataCollectParam
import com.fan.service.ContentAnalysisService
import com.fan.service.SearchByCodeCollector
import org.apache.coyote.BadRequestException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/data")
class DataController(
    private val searchByCodeCollector: SearchByCodeCollector,
    private val contentAnalysisService: ContentAnalysisService
) {

    @PostMapping
    fun startCollect(
        @RequestBody dataCollectParam: DataCollectParam,
    ): Int {
        try {
            if (DateUtil.parseDate(dataCollectParam.tillDate) > DateUtil.date()) {
                throw BadRequestException("请输入有效日期")
            }
            return searchByCodeCollector.startCollect(dataCollectParam, SearchType.CODE)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
