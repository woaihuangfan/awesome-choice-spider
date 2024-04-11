package com.fan.extractor

object AccountCompanyNameFilter {

    fun filter(names: List<String>): String {

        return names.distinct().filter { it.length >= 8 }
            .first { it.endsWith("合伙）") || it.endsWith("事务所") }
    }

    fun isValid(name: String): Boolean {
        return name.length >= 8 && (name.endsWith("合伙）") || name.endsWith("事务所"))
    }
}