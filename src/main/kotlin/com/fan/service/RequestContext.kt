package com.fan.service

import java.util.UUID
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class RequestContext(
    val requestId: String,
) : AbstractCoroutineContextElement(RequestContext) {
    companion object Key : CoroutineContext.Key<RequestContext> {
        fun get() = RequestContext(UUID.randomUUID().toString())

        fun getRequestId(context: RequestContext): String {
            val requestId = context[RequestContext]?.requestId ?: throw RuntimeException("null request id")
            return requestId
        }
    }
}
