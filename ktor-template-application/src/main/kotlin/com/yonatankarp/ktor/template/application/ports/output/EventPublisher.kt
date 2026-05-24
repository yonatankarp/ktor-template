package com.yonatankarp.ktor.template.application.ports.output

import com.yonatankarp.ktor.template.domain.event.DomainEvent

interface EventPublisher<in T : DomainEvent> {
    suspend fun publish(event: T)
}
