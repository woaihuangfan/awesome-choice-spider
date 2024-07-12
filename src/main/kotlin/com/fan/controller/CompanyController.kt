package com.fan.controller

import cn.hutool.poi.excel.ExcelUtil
import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.db.entity.Company
import com.fan.db.entity.CompanyFile
import com.fan.db.repository.CompanyFileRepository
import com.fan.db.repository.CompanyRepository
import com.fan.util.ExcelHelper
import jakarta.servlet.http.HttpServletResponse
import org.apache.coyote.BadRequestException
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/company")
class CompanyController(
    private val companyRepository: CompanyRepository,
    private val companyFileRepository: CompanyFileRepository,
) {
    companion object {
        private const val STOCK_CODE = "证券代码"
    }

    @GetMapping("fileName")
    fun getUploadedFileName(): ResponseEntity<String> = ResponseEntity.ok().body(getFileName())

    private fun getFileName(): String {
        val files = companyFileRepository.findAll()
        if (files.isNotEmpty()) {
            return files.first().fileName
        }
        return ""
    }

    @PostMapping
    fun import(
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<Map<String, Any>> {
        var count = 0L
        try {
            validateFileName(file)
            file.inputStream.use { inputStream ->
                val reader = ExcelUtil.getReader(inputStream)
                val sources = reader.readAll().map { it.values }.flatten()
                letPeopleKnow("文件读取成功")
                validateContent(sources)
                val companies =
                    sources.map {
                        val code = it.toString().substring(0, 6)
                        Company(stock = code)
                    }
                count = save(companies)
            }
        } catch (e: BadRequestException) {
            return ResponseEntity.ok().body(mapOf("message" to "导入格式有误，请重新导入", "count" to 0L))
        }
        if (count > 0) {
            companyFileRepository.deleteAll()
            companyFileRepository.save(CompanyFile(fileName = file.originalFilename.orEmpty()))
        }
        return ResponseEntity.ok().body(mapOf("message" to "success", "count" to count))
    }

    private fun validateFileName(file: MultipartFile) {
        if (file.originalFilename.isNullOrBlank()) {
            throw BadRequestException("文件名无效，请重新导入")
        }
    }

    private fun validateContent(sources: List<Any>) {
        sources.forEachIndexed { index, it ->
            println("校验第 $index 行内容")
            if (it.toString().length < 6) {
                throw BadRequestException("导入内容格式有误，请重新导入")
            }
        }
    }

    private fun save(companies: List<Company>): Long {
        companyRepository.deleteAll()
        companyRepository.saveAll(companies)
        val size = companies.size
        letPeopleKnow("成功导入${size}笔数据")
        return companyRepository.count()
    }

    @GetMapping
    fun download(httpServletResponse: HttpServletResponse) {
        val results = companyRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
        val headers =
            listOf(
                STOCK_CODE,
            )
        val contents =
            results.map {
                listOfNotNull(
                    it.stock,
                )
            }
        val writer = ExcelHelper.writeRows(headers, contents)
        ExcelHelper.flushToResponse(httpServletResponse, writer, getFileName())
    }
}
