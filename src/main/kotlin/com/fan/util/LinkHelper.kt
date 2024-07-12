package com.fan.util

import com.fan.client.NoticeDetailClient.getDetailPageUrl

object LinkHelper {
    fun addHyperLinkAndReturn(
        stock: String,
        code: String,
        text: String,
    ) = "<a target='_blank' style='color: blue; text-decoration: none; font-weight: bold;' href='%s'>《%s》</a>".format(
        getDetailPageUrl(stock, code),
        text,
    )
}
