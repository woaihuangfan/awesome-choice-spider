package com.fan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent
import org.springframework.context.ApplicationListener

@SpringBootApplication
class AwesomeChoiceSpiderApplication : ApplicationListener<ServletWebServerInitializedEvent> {

    override fun onApplicationEvent(event: ServletWebServerInitializedEvent) {
        val port = event.webServer.port
        println("Derby database is running on port: $port")
    }
}

fun main(args: Array<String>) {
    runApplication<AwesomeChoiceSpiderApplication>(*args)
}
