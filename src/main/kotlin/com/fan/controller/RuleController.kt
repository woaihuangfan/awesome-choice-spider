package com.fan.controller

import com.fan.db.entity.TitleFilterRule
import com.fan.db.repository.TitleFilterRuleRepository
import com.fan.dto.PageResult
import com.fan.po.TitleFilterRulePO
import com.fan.po.Type
import org.apache.coyote.BadRequestException
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/rule")
class RuleController(
    private val titleFilterRuleRepository: TitleFilterRuleRepository
) {

    @PostMapping
    fun add(
        @RequestBody @Validated titleFilterRulePO: TitleFilterRulePO
    ): ResponseEntity<String> {
        titleFilterRuleRepository.save(
            TitleFilterRule(
                keyword = titleFilterRulePO.keyword,
                type = parseType(titleFilterRulePO.type)
            )
        )
        return ResponseEntity.ok().body("添加成功")
    }

    private fun parseType(type: String): String {
        return when (type) {
            Type.INCLUDE.typeName -> Type.INCLUDE.typeName
            Type.EXCLUDE.typeName -> Type.EXCLUDE.typeName
            else -> throw BadRequestException("invalid type: $type")
        }
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable("id") id: Long
    ): String {
        titleFilterRuleRepository.deleteById(id)
        return "删除成功"
    }

    @GetMapping
    fun list(
        @RequestParam page: Int,
        @RequestParam limit: Int,
        @RequestParam type: String,
    ): PageResult {
        val pageable: PageRequest = PageRequest.of(page - 1, limit)
        return PageResult.success(titleFilterRuleRepository.findByTypeIs(type.lowercase(), pageable))
    }
}
