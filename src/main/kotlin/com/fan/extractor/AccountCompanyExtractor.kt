package com.fan.extractor

object AccountCompanyExtractor {
    fun extractAccountCompanyName(notice: String, code: String): String {
        var result = notice.substringAfter("会计师事务所名称：").substringBefore("成立日期：")
        if (result.length > 30 || result.length < 5) {
            result = notice.substringAfter("机构名称：").substringBefore("成立日期：")
        }
        if (result.length > 30 || result.length < 5) {
            result = notice.substringAfter("同意续聘").substringBefore("为公司 ")
        }
        if (result.length > 30 || result.length < 5) {
            result = notice.substringAfter("续聘").substringBefore("为")
        }
        if (!result.contains("事务所")) {
            result = "解析失败 code:$code"
        }
        if (!result.trim().endsWith("）")) {
            result = "解析失败 code:$code"
        }
        if (result.length > 30) {
            result = "解析失败 code:$code"
        }
        return result
    }
}