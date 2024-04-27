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
        return listOf(
            "对",
            "会计师事务所",
            "基本",
            "名称",
            "任具有证券从业资格的",
            "拟继续聘请",
            "公司",
            "原审计机构",
            "事项",
            "情况",
            "的",
            "说明",
            "公司",
            "拟聘",
            "同意",
            "聘任",
        )
    }

    fun getAllReplaceableWords(): List<String> {
        return listOf(
            "拟聘任的境内会计师事务所",
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
            "\\n",
            ":"
        ).sortedByDescending { it.length }
    }
}