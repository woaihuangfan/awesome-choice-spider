package com.fan.controller

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ops")
class ShutdownController : ApplicationContextAware {
    private lateinit var context: ApplicationContext

    @GetMapping("/shutdown")
    fun shutdownContext() {
        val configurableApplicationContext = context as ConfigurableApplicationContext
        configurableApplicationContext.close()
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.context = applicationContext
    }
}
