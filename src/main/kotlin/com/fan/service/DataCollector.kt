package com.fan.service

import com.fan.enums.SearchType

fun interface DataCollector {
    fun start(param: String, type: SearchType):String
}