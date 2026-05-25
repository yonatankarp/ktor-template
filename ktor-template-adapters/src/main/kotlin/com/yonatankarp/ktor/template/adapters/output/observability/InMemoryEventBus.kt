package com.yonatankarp.ktor.template.adapters.output.observability

import com.yonatankarp.ktor.template.application.ports.output.EventPublisher
import com.yonatankarp.ktor.template.domain.event.DomainEvent
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.slf4j.LoggerFactory

class InMemoryEventBus<T : DomainEvent>(
    private val meterRegistry: MeterRegistry,
) : EventPublisher<T> {
    val events: SharedFlow<T>
        field: MutableSharedFlow<T> = MutableSharedFlow(extraBufferCapacity = EVENT_BUFFER_CAPACITY)

    override fun publish(event: T) {
        if (events.tryEmit(event)) return
        val eventName = event::class.simpleName ?: "Unknown"
        log.warn("Dropped domain event due to buffer overflow: event={}", eventName)
        meterRegistry.counter(DROPPED_EVENTS_METRIC, "event", eventName).increment()
    }

    private companion object {
        private const val EVENT_BUFFER_CAPACITY = 64
        private const val DROPPED_EVENTS_METRIC = "domain.events.dropped"
        private val log = LoggerFactory.getLogger(InMemoryEventBus::class.java)
    }
}
