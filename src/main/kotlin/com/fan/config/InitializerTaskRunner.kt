package com.fan.config

import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.core.util.NumberUtil.isNumber
import cn.hutool.poi.excel.ExcelUtil
import com.fan.db.entity.Company
import com.fan.db.entity.TitleFilterRule
import com.fan.db.repository.CompanyRepository
import com.fan.db.repository.TitleFilterRuleRepository
import com.fan.po.Type
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

@Component
class InitializerTaskRunner(
    private val companyRepository: CompanyRepository, private val titleFilterRuleRepository: TitleFilterRuleRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
//        addCompany()
        initDefaultTitleRule()
    }

    private fun initDefaultTitleRule() {
        getInitialTitleFilterRules().forEach {
            val existedKeywords = titleFilterRuleRepository.findAllByKeyword(it.keyword)
            if (CollectionUtils.isEmpty(existedKeywords)) {
                titleFilterRuleRepository.save(it)
            }
        }

    }

    private fun getInitialTitleFilterRules(): List<TitleFilterRule> {
        return getTitleKeywords().filter { it.isNotEmpty() }
            .map { TitleFilterRule(keyword = it, type = Type.INCLUDE.typeName) }
    }

    private fun getTitleKeywords(): List<String> {
        return listOf()
    }

    private fun addCompany() {
        val stocks = readStocks()
        stocks.forEach { stock ->
            val exist = companyRepository.findByStock(stock)
            if (exist == null) {
                companyRepository.save(Company(stock = stock))
            }
        }
    }

    private fun readStocks(): ArrayList<String> {
        val codeStream = ClassPathResource("classpath:codes.xlsx").stream
        val codes = ArrayList<String>()
        ExcelUtil.readBySax(
            codeStream, 0
        ) { _, rowIndex, rowCells ->
            if (rowIndex > 0) {
                val cell = rowCells.first() as String
                if (cell.length > 6) {
                    val code = cell.substring(0, 6)
                    if (isNumber(code)) {
                        codes.add(code)
                    }
                }

            }
        }
        return codes
    }
}