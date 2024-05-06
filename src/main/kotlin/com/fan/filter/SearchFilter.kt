package com.fan.filter

import com.fan.response.Item

interface SearchFilter {

    fun doFilter(item: Item): Boolean {
        return false
    }
}