package com.fan.service

import com.fan.enums.SearchType

fun interface DataCollector {
    fun startCollect(param: String, type: SearchType):String
}