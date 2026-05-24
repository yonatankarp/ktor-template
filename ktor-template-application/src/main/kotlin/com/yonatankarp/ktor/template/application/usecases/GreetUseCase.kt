package com.yonatankarp.ktor.template.application.usecases

import com.yonatankarp.ktor.template.application.ports.input.Greet
import com.yonatankarp.ktor.template.application.ports.output.EventPublisher
import com.yonatankarp.ktor.template.application.ports.output.GreetingCatalog
import com.yonatankarp.ktor.template.domain.event.GreetingDelivered
import com.yonatankarp.ktor.template.domain.valueobject.Greeting
import java.time.Instant

class GreetUseCase(
    private val catalog: GreetingCatalog,
    private val events: EventPublisher<GreetingDelivered>,
) : Greet {
    override suspend fun invoke(): Greeting =
        (catalog.random() ?: DEFAULT_GREETING).also {
            events.publish(GreetingDelivered(it, Instant.now()))
        }

    companion object {
        private val DEFAULT_GREETING = Greeting(language = "en", message = "Hello, World!")
    }
}
