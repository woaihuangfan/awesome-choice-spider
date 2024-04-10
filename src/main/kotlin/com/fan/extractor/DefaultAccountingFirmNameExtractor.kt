package com.fan.extractor

import java.util.regex.Pattern


object DefaultAccountingFirmNameExtractor {
    fun extractAccountingFirmName(announcement: String): String {
        try {
            val accountingFirmNames = mutableListOf<String>()
            val patterns = listOf(
                "(?<=会计师事务所名称：)[\\u4e00-\\u9fa5]+事务所",
                "事务所名称：(.*?)（特殊普通合伙）",
                "(?<=聘)[\\u4e00-\\u9fa5]+会计师事务所",
                "(?<=续聘)[\\u4e00-\\u9fa5]+会计师事务所",
                "(?<=事务所名称：)[\\u4e00-\\u9fa5]+事务所",
                "(?<=机构名称：)[\\u4e00-\\u9fa5]+",
                "(?<=续聘)[\\u4e00-\\u9fa5]+",
                "(?<=同意续聘)[\\u4e00-\\u9fa5]+(?=为公司)",
                "(?<=续聘)[\\u4e00-\\u9fa5]+(?=为)",
            )


            for (pattern in patterns) {
                val matcher = Pattern.compile(pattern).matcher(announcement)
                while (matcher.find()) {
                    val name = matcher.group()
                    if (!name.startsWith("的") && !name.startsWith("请")) {
                        accountingFirmNames.add(name.replace("事务所名称：", ""))
                    }
                }
            }


            return accountingFirmNames.distinct().filter { it.length > 8 }
                .first { it.endsWith("合伙）") || it.endsWith("事务所") }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""

    }

}