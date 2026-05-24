package com.yonatankarp.ktor.template.domain.event

import java.time.Instant

sealed interface DomainEvent {
    val occurredAt: Instant
}
