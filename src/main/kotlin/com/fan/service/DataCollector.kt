package com.fan.service

import com.fan.enums.SearchType
import com.fan.po.DataCollectParam

fun interface DataCollector {
    fun startCollect(param: DataCollectParam, type: SearchType):String
}