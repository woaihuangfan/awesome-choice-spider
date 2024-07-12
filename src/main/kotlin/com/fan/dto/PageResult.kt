package com.fan.dto

import org.springframework.data.domain.Page

data class PageResult(
    val code: String,
    val msg: String,
    val count: Long? = 0,
    val data: Any? = null,
) {
    companion object {
        fun <T> success(page: Page<T>): PageResult = PageResult("0", "success", page.totalElements, page.content)

        fun error(message: String): PageResult = PageResult("500", message)
    }
}
