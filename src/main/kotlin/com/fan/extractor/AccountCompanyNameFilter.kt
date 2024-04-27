package com.fan.extractor

object AccountCompanyNameFilter {

    fun filter(names: List<String>): String? {

        try {
            return names.distinct().first { isValidAccountName(it) }
        } catch (e: NoSuchElementException) {
//           ignore
        }
        return null
    }

    fun isValidAccountName(name: String): Boolean {
        return lengthCheck(name) && mustHaveCheck(name) && isNotReplaceable(name) && shouldNotContainsCheck(name)
    }

    private fun lengthCheck(name: String) = name.length in 8..50

    private fun mustHaveCheck(name: String) = (name.endsWith("合伙）") || name.endsWith("事务所"))

    private fun shouldNotContainsCheck(name: String): Boolean {
        getRandomCheckWords().forEach {
            if (name.contains(it)) {
                return false
            }
        }
        return true
    }

    private fun getRandomCheckWords(): List<String> {
        return listOf("审计机构", "20", "，", "一")
    }

    private fun isNotReplaceable(name: String): Boolean {
        getAllReplaceableWords().forEach {
            if (name.contains(it)) {
                return false
            }
        }

        getFirstReplaceableWords().forEach {
            if (name.startsWith(it)) {
                return false
            }
        }
        return true
    }

    fun getFirstReplaceableWords(): List<String> {
        return getAllReplaceableWords() + listOf(
            "同意续聘任",
            "对",
            "会计师事务所",
            "基本",
            "名称",
            "任具有证券从业资格的",
            "拟继续聘请",
            "拟继续聘用",
            "继续聘用",
            "公司",
            "原审计机构",
            "事项",
            "情况",
            "具有证券从业资格",
            "的",
            "说明",
            "公司",
            "拟聘",
            "同意",
            "聘任",
            "审阅了",
            "关于聘任",
            "请",
            "信息",
            "为",
            "根据",
            "拟",
        )
    }

    fun getAllReplaceableWords(): List<String> {
        return listOf(
            "拟聘任的境内会计师事务所",
            "拟聘任会计师事务所事项的情况说明",
            "续聘会计师事务所的情况说明",
            "拟聘任会计师事务所的名称",
            "拟聘任的会计师事务所名称",
            "拟聘任的会计师事务所名称为",
            "会计师事务所的情况说明",
            "拟续聘的会计师事务所",
            "事务所名称",
            "基本情况",
            "名称",
            "审核委员会对",
            "基本信息",
            "1、",
            "（一）",
            "1.",
            "机构信息",
            "（1）",
            "：",
            "机构名称",
            "1．",
            "1. ",
            "：",
            "公司拟续聘",
            "会计师事务所事项的基本情况",
            "会计师事务所事项的情况说明",
            "会计师事务所的基本情况",
            "。",
            "拟续聘的会计师事务所名称",
            "拟续聘的会计师事务所名称",
            "经审核，",
            "经核查，",
            "（特殊普通合伙）",
            "鉴于",
            "续聘",
            "拟聘",
            "上市公司",
            "成立的",
            "财务报告",
            "同意继续聘任",
            "公司拟继续聘任",
            "拟继续聘任",
            "为公司",
            "会计师事务所名称",
            "同意公司继续聘任",
            "担任公司",
            "继续聘任",
            "关于续聘",
            "公司拟聘任",
            "\\n",
            ":",
            "（",
            "1",
            "）",
            "机构名称",
            "：",
            "“",
        ).sortedByDescending { it.length }
    }
}