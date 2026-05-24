package com.yonatankarp.ktor.template.adapters.output.observability

import com.yonatankarp.ktor.template.domain.event.GreetingDelivered
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("com.yonatankarp.ktor.template.GreetingDeliveredLogger")

fun CoroutineScope.logGreetingDeliveries(bus: InMemoryEventBus<GreetingDelivered>) {
    launch {
        bus.events.collect { event ->
            log.info(
                "Greeting delivered: language={}, message='{}', at={}",
                event.greeting.language,
                event.greeting.message,
                event.occurredAt,
            )
        }
    }
}
