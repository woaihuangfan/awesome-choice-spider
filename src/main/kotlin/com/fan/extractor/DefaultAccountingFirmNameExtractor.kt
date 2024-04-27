package com.fan.extractor

import java.util.regex.Pattern


object DefaultAccountingFirmNameExtractor {
    fun extractAccountingFirmName(announcement: String): String {
        try {
            val accountingFirmNames = mutableListOf<String>()
            val patterns = listOf(
                "拟聘任会计师事务所的名称(.*?)（特殊普通合伙）",
                "同意继续聘任(.*?)（特殊普通合伙）",
                "同意公司继续聘任(.*?)（特殊普通合伙）",
                "公司拟继续聘任(.*?)（特殊普通合伙）",
                "同意续聘任(.*?)（特殊普通合伙）",
                "拟继续聘任(.*?)（特殊普通合伙）",
                "公司拟聘任(.*?)（特殊普通合伙）",
                "关于聘任(.*?)（特殊普通合伙）担任公司",
                "关于续聘(.*?)（特殊普通合伙）担任公司",
                "继续聘任(.*?)（特殊普通合伙）担任公司",
                "拟继续聘任(.*?)为公司",
                "拟聘任的会计师事务所名称(.*?)（特殊普通合伙）",
                "拟聘任的会计师事务所名称为(.*?)（特殊普通合伙）",
                "拟聘任会计师事务所事项的情况说明(.*?)（特殊普通合伙）",
                "拟续聘的会计师事务所名称：(.*?)（特殊普通合伙）",
                "会计师事务所名称：(.*?)（特殊普通合伙）",
                "拟续聘的会计师事务所名称:(.*?)（特殊普通合伙）",
                "拟聘任会计师事务所的名称:(.*?)（特殊普通合伙）",
                "续聘会计师事务所的情况说明(.*?)（特殊普通合伙）",
                "拟聘任的境内会计师事务所：(.*?)合伙）",
                "拟聘任的境内会计师事务所：(.*?)事务所）",
                "拟聘任的境内会计师事务所:(.*?)合伙）",
                "拟聘任的境内会计师事务所:(.*?)事务所）",
                "续聘会计师事务所的情况说明(.*?)事务所）",
                "拟续聘的会计师事务所：(.*?)合伙）",
                "拟继续聘请(.*?)（特殊普通合伙）",
                "拟继续聘用(.*?)（特殊普通合伙）",
                "拟继续聘请(.*?)事务所）",
                "(?<=同意续聘)[\\u4e00-\\u9fa5]+(?=（特殊普通合伙）)",
                "(?<=拟聘任的会计师事务所名称：)[\\u4e00-\\u9fa5]+(?=（特殊普通合伙）)",
                "(?<=同意续聘)[\\u4e00-\\u9fa5]+(?=为公司)",
                "(?<=继续聘任)[\\u4e00-\\u9fa5]+(?=为公司)",
                "(?<=同意聘任)[\\u4e00-\\u9fa5]+(?=（特殊普通合伙）)",
                "(?<=续聘)[\\u4e00-\\u9fa5]+(?=（特殊普通合伙）)",
                "同意聘任(.*?)合伙）",
                "(?<=会计师事务所名称：)[\\u4e00-\\u9fa5]+事务所",


                "事务所名称：(.*?)（特殊普通合伙）",
                "事务所名称(.*?)（特殊普通合伙）",
                "审核委员会对(.*?)（特殊普通合伙）",
                "机构信息(.*?)事务所",
                "机构信息1、基本信息(.*?)事务所",
                "（一）基本信息(.*?)事务所",
                "1、基本信息(.*?)事务所",
                "（1）机构名称：(.*?)事务所",
                "机构信息1、基本信息(.*?)（特殊普通合伙）",
                "（一）基本信息(.*?)（特殊普通合伙）",
                "1、基本信息(.*?)（特殊普通合伙）",
                "（1）机构名称：(.*?)（特殊普通合伙）",
                "基本信息(.*?)（特殊普通合伙）",
                "(?<=续聘)[\\u4e00-\\u9fa5]+(?=为)",
                "经审核，(.*?)（特殊普通合伙）",
                "经核查，(.*?)（特殊普通合伙）",
                "(?<=续聘)[\\u4e00-\\u9fa5]+会计师事务所",
                "(?<=聘请)[\\u4e00-\\u9fa5]+会计师事务所",
                "(?<=事务所名称：)[\\u4e00-\\u9fa5]+事务所",
                "(?<=事务所名称)[\\u4e00-\\u9fa5]+事务所",
                "(?<=经审查，)[\\u4e00-\\u9fa5]+合伙）",
                "(?<=机构名称：)[\\u4e00-\\u9fa5]+",
                "(?<=续聘)[\\u4e00-\\u9fa5]+",
                "经审核，(.*?)事务所）",
                "经核查，(.*?)事务所）",
                "审阅了(.*?)（特殊普通合伙）",
                "对(.*?)事务所）",
                "对(.*?)（特殊普通合伙）",
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
                .orEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""

    }

    private fun replace(name: String): String {
        var result = name
        AccountCompanyNameFilter.getFirstReplaceableWords().forEach {
            if (result.startsWith(it)) {
                result = result.replaceFirst(it, "")
            }
        }
        AccountCompanyNameFilter.getAllReplaceableWords().forEach {
            result = result.replace(it, "")
        }

        return result
    }


}