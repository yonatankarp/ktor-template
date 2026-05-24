package com.yonatankarp.ktor.template.adapters.output.observability

import com.yonatankarp.ktor.template.application.ports.output.EventPublisher
import com.yonatankarp.ktor.template.domain.event.DomainEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class InMemoryEventBus<T : DomainEvent> : EventPublisher<T> {
    val events: SharedFlow<T>
        field: MutableSharedFlow<T> = MutableSharedFlow(extraBufferCapacity = EVENT_BUFFER_CAPACITY)

    override fun publish(event: T): Boolean = events.tryEmit(event)

    private companion object {
        private const val EVENT_BUFFER_CAPACITY = 64
    }
}
