package com.fan.extractor

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DefaultAccountingFirmNameExtractorTest{

    @Test
    fun name() {
        println(DefaultAccountingFirmNameExtractor.extractAccountingFirmName("""
            科博达技术股份有限公司 审计委员会关于续聘会计师事务所的书面审核意见 根据《上海证券交易所股票上市规则（2023 年修订）》、《上海证券交易所上市公司关联交易实施指引》及《公司章程》等有关规定，作为科博达技术股份有限公司（以下简称“公司”）董事会审计委员会委员，我们全面、认真地审查了公司关于续聘会计师事务所的事项，审阅了众华会计师事务所（特殊普通合伙）（以下简称“众华”）提供的相关资料，并对以往年度众华在公司审计工作中的表现进行评估，发表如下审核意见： 众华具备证券、期货相关业务审计资格，具有丰富的执业经验，能够满足公司财务审计和相关专项审计工作的要求；众华已购买职业保险，且相关职业保险能够覆盖因审计失败导致的民事赔偿责任，具备投资者保护能力；众华及项目成员不存在违反《中国注册会计师职业道德守则》对独立性要求的情形。 综上，审计委员会认为众华能够满足公司未来审计工作的需求，建议续聘众华作为公司 2024 年度财务审计机构和内部控制审计机构，并同意将上述事项提交公司董事会审议。 委员：吕勇、马钧、孙林 二〇二四年四月十日

        """.trimIndent()))
    }
}