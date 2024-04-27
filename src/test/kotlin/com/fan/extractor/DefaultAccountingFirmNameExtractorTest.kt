package com.fan.extractor

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DefaultAccountingFirmNameExtractorTest{

    @Test
    fun name() {
        println(DefaultAccountingFirmNameExtractor.extractAccountingFirmName("""
           
        """.trimIndent()))
    }
}