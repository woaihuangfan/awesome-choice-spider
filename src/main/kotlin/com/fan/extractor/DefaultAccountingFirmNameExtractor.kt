package com.fan.extractor

import java.util.regex.Pattern


object DefaultAccountingFirmNameExtractor {
    fun extractAccountingFirmName(announcement: String): String {
        try {
            val accountingFirmNames = mutableListOf<String>()
            val patterns = listOf(
                "(?<=会计师事务所名称：)[\\u4e00-\\u9fa5]+事务所",
                "事务所名称：(.*?)（特殊普通合伙）",
                "审核委员会对(.*?)（特殊普通合伙）",
                "机构信息1、基本信息(.*?)事务所",
                "（一）基本信息(.*?)事务所",
                "1、基本信息(.*?)事务所",
                "（1）机构名称：(.*?)事务所",
                "机构信息1、基本信息(.*?)（特殊普通合伙）",
                "（一）基本信息(.*?)（特殊普通合伙）",
                "1、基本信息(.*?)（特殊普通合伙）",
                "（1）机构名称：(.*?)（特殊普通合伙）",
                "基本信息(.*?)（特殊普通合伙）",
                "拟续聘的会计师事务所：(.*?)合伙）",
                "(?<=聘)[\\u4e00-\\u9fa5]+会计师事务所",
                "(?<=续聘)[\\u4e00-\\u9fa5]+会计师事务所",
                "(?<=聘请)[\\u4e00-\\u9fa5]+会计师事务所",
                "(?<=事务所名称：)[\\u4e00-\\u9fa5]+事务所",
                "(?<=机构名称：)[\\u4e00-\\u9fa5]+",
                "(?<=续聘)[\\u4e00-\\u9fa5]+",
                "(?<=同意续聘)[\\u4e00-\\u9fa5]+(?=为公司)",
                "(?<=续聘)[\\u4e00-\\u9fa5]+(?=为)",
            )


            for (pattern in patterns) {
                val matcher =
                    Pattern.compile(pattern).matcher(announcement.replace(" ", "").replace("\r", "").replace("\n", ""))
                while (matcher.find()) {
                    val name = matcher.group()
                    if (!name.startsWith("的") && !name.startsWith("请")) {
                        accountingFirmNames.add(
                            replace(name)
                        )
                    }
                }
            }

            return AccountCompanyNameFilter.filter(accountingFirmNames)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""

    }

    private fun replace(name: String) =
        name.replace("事务所名称", "").replace("拟续聘的会计师事务所", "").replace("审核委员会对", "")
            .replace("基本信息", "").replace("1、", "").replace("（一）", "").replace("1.", "")
            .replace("机构信息", "").replace("（1）", "").replace("：", "").replace("机构名称", "").replace("1．", "")

}