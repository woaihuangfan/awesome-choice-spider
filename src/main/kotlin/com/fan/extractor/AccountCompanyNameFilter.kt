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
        return name.length in 8..50 && (name.endsWith("合伙）") || name.endsWith("事务所")) && !name.contains("，") && isNotReplaceable(
            name
        )
    }

    private fun isNotReplaceable(name: String): Boolean {
        getReplaceableWords().forEach {
            if (name.contains(it)) {
                return false
            }
        }
        return true
    }

    fun getReplaceableWords(): List<String> {
        return listOf(
            "事务所名称",
            "拟续聘的会计师事务所",
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
            "拟聘任的境内会计师事务所",
            "：",
            "会计师事务所的情况说明",
            "公司拟续聘",
            "会计师事务所事项的基本情况",
            "会计师事务所事项的情况说明",
            "会计师事务所的基本情况",
            "。",
            "拟续聘的会计师事务所名称",
            "经审核，",
            "经核查，",
            ":"
        )
    }
}