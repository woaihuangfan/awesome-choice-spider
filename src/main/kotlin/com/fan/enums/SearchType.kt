package com.fan.enums

enum class SearchType(
    val typeName: String,
) {
    KEYWORD("keyword"),
    CODE("code"), ;

    companion object {
        fun from(type: String): SearchType = SearchType.valueOf(type.uppercase())
    }
}
