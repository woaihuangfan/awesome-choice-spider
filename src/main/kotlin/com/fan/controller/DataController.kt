package com.fan.controller

import cn.hutool.core.date.DateUtil
import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.db.repository.TitleFilterRuleRepository
import com.fan.enums.SearchType
import com.fan.po.DataCollectParam
import com.fan.service.AbstractDataCollector
import com.fan.service.SearchByCodeCollector
import org.apache.coyote.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/data")
class DataController(
    private val searchByCodeCollector: SearchByCodeCollector,
    private val titleFilterRuleRepository: TitleFilterRuleRepository,
    private val clearController: ClearController
) {

    @GetMapping("/status")
    fun getStatus(): ResponseEntity<String> {
        return ResponseEntity.ok(searchByCodeCollector.getStatus().toString())
    }

    @PostMapping
    fun startCollect(
        @RequestBody dataCollectParam: DataCollectParam,
    ): ResponseEntity<String> {
        try {
            if (DateUtil.parseDate(dataCollectParam.tillDate) > DateUtil.date()) {
                throw BadRequestException("请输入有效日期")
            }

            if (titleFilterRuleRepository.findAll().isEmpty()) {
                throw BadRequestException("请先添加标题过滤关键字")
            }

            if (searchByCodeCollector.getStatus() == AbstractDataCollector.Status.RUNNING) {
                letPeopleKnow("有采集任务正在进行中，将终止当前采集任务清除数据并重新采集")
                clearController.cancelAndClear()
            }
            return ResponseEntity.ok(searchByCodeCollector.startCollect(dataCollectParam, SearchType.CODE).toString())

        } catch (e: BadRequestException) {
            e.printStackTrace()
            return ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误")
        }
    }
}
