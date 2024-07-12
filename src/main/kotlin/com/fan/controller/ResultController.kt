package com.fan.controller

import com.fan.db.repository.CollectLogRepository
import com.fan.dto.TillDateDTO
import com.fan.po.EditResultParam
import com.fan.service.AnalysisLogService
import com.fan.service.RequestContext
import com.fan.service.ResultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/result")
class ResultController(
    private val resultService: ResultService,
    private val analysisLogService: AnalysisLogService,
    private val collectLogRepository: CollectLogRepository,
) {
    @PatchMapping("/{id}")
    fun edit(
        @PathVariable id: Long,
        @RequestBody editResultParam: EditResultParam,
    ): String {
        val resultOptional = resultService.findById(id)
        val result = resultOptional.orElseThrow()
        result.accountCompanyName = editResultParam.accountCompanyName.orEmpty()
        resultService.save(result)
        analysisLogService.saveAnalysisLog("手动调整提取结果", RequestContext.get())
        return "修改成功"
    }

    @GetMapping(value = ["/tillDates"])
    fun getTillDates(): ResponseEntity<List<TillDateDTO>> {
        val collectLogs = collectLogRepository.findAll()
        val tillDateDTOS =
            collectLogs
                .filter { it.tillDate.isNotBlank() }
                .map { it.tillDate }
                .distinct()
                .map { TillDateDTO(it, it) }
        return ResponseEntity.ok(tillDateDTOS)
    }
}
