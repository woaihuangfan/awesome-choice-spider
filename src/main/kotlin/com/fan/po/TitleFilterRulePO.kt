package com.fan.po

data class TitleFilterRulePO(
    val keyword: String,
    val type: String,
)

sealed class Type(
    val typeName: String,
) {
    data object INCLUDE : Type("include")

    data object EXCLUDE : Type("exclude")
}
