package com.fan.po

import org.jetbrains.annotations.NotNull

data class DataCollectParam(
    @NotNull
    val tillDate: String,
    val keyword: String = "",
)
