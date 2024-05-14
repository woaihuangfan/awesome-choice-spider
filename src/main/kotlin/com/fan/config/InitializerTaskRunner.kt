package com.fan.config

import com.fan.db.entity.TitleFilterRule
import com.fan.db.repository.TitleFilterRuleRepository
import com.fan.po.Type
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

@Component
class InitializerTaskRunner(
    private val titleFilterRuleRepository: TitleFilterRuleRepository,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
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

    private fun getInitialTitleFilterRules(): List<TitleFilterRule> =
        getTitleKeywords()
            .filter { it.isNotEmpty() }
            .map { TitleFilterRule(keyword = it, type = Type.INCLUDE.typeName) }

    private fun getTitleKeywords(): List<String> = listOf()


}
