package com.fan.controller

import com.fan.po.EditResultParam
import com.fan.service.AnalysisLogService
import com.fan.service.ResultService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("/result")
class ResultController(
    private val resultService: ResultService,
    private val analysisLogService: AnalysisLogService
) {

    @PatchMapping("/{id}")
    fun edit(
        @PathVariable id: Long, @RequestBody editResultParam: EditResultParam
    ): String {
        val resultOptional = resultService.findById(id)
        val result = resultOptional.orElseThrow()
        result.accountCompanyName = editResultParam.accountCompanyName
        resultService.save(result)
        analysisLogService.saveAnalysisLog("手动调整提取结果", UUID.randomUUID().toString(),)
        return "修改成功"
    }


}
