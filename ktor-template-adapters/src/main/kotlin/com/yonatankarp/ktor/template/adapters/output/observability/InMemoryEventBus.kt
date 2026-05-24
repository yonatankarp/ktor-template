package com.yonatankarp.ktor.template.adapters.output.observability

import com.yonatankarp.ktor.template.application.ports.output.EventPublisher
import com.yonatankarp.ktor.template.domain.event.DomainEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class InMemoryEventBus<T : DomainEvent> : EventPublisher<T> {
    val events: SharedFlow<T>
        field: MutableSharedFlow<T> =
        MutableSharedFlow(
            extraBufferCapacity = EVENT_BUFFER_CAPACITY,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    override suspend fun publish(event: T) {
        events.tryEmit(event)
    }

    private companion object {
        private const val EVENT_BUFFER_CAPACITY = 64
    }
}
