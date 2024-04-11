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
        return name.length in 8..50 && (name.endsWith("合伙）") || name.endsWith("事务所"))
    }
}