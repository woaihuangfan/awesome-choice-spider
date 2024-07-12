package com.fan.db.repository

fun interface CountByRequestIdRepository {
    fun countByRequestId(requestId: String): Int
}
