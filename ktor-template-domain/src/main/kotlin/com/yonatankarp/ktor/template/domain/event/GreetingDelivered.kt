package com.yonatankarp.ktor.template.domain.event

import com.yonatankarp.ktor.template.domain.valueobject.Greeting
import java.time.Instant

data class GreetingDelivered(
    val greeting: Greeting,
    override val occurredAt: Instant,
) : DomainEvent
