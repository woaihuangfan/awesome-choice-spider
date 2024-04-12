package com.fan.controller

import cn.hutool.core.date.DateUtil
import com.fan.enums.SearchType
import com.fan.po.DataCollectParam
import com.fan.service.ContentAnalysisService
import com.fan.service.SearchByCodeCollector
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
        @RequestBody dataCollectParam: DataCollectParam
    ): String {
        try {
            if (DateUtil.parseDate(dataCollectParam.tillDate) > DateUtil.date()) {
                return "请输入有效日期"
            }
            searchByCodeCollector.startCollect(dataCollectParam, SearchType.CODE)
        } catch (e: Exception) {
            return e.message.orEmpty()
        }
        return "开始采集，请稍后查询"
    }
}
