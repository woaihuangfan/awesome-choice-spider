package com.fan.controller

import com.fan.db.repository.ResultRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/excel")
class ExcelController(
    private val resultRepository: ResultRepository
) {

    @GetMapping(value = ["/result"])
    fun start(httpServletResponse: HttpServletResponse) {

        val results = resultRepository.findAll()

    }
}