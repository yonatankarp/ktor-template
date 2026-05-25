package com.yonatankarp.ktor.template.adapters.output.observability

import com.yonatankarp.ktor.template.domain.event.GreetingDelivered
import com.yonatankarp.ktor.template.domain.valueobject.Greeting
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Instant

class InMemoryEventBusTest :
    FunSpec({

        test("increments the drop counter when the buffer overflows") {
            // Given
            val registry = SimpleMeterRegistry()
            val bus = InMemoryEventBus<GreetingDelivered>(registry)
            val event = GreetingDelivered(Greeting("en", "Hi"), Instant.parse("2026-01-01T00:00:00Z"))

            coroutineScope {
                val subscriber =
                    launch(start = CoroutineStart.UNDISPATCHED) {
                        bus.events.collect { awaitCancellation() }
                    }

                // When
                repeat(65) { bus.publish(event) }

                // Then
                registry
                    .counter("domain.events.dropped", "event", "GreetingDelivered")
                    .count() shouldBe 1.0

                subscriber.cancel()
            }
        }
    })
