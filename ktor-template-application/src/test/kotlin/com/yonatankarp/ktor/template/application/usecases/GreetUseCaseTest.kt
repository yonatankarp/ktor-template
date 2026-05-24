package com.yonatankarp.ktor.template.application.usecases

import com.yonatankarp.ktor.template.application.ports.output.EventPublisher
import com.yonatankarp.ktor.template.application.ports.output.GreetingCatalog
import com.yonatankarp.ktor.template.domain.event.GreetingDelivered
import com.yonatankarp.ktor.template.domain.valueobject.Greeting
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class GreetUseCaseTest :
    FunSpec({

        test("returns greeting from catalog and publishes GreetingDelivered") {
            // Given
            val catalog = mockk<GreetingCatalog>()
            val events = mockk<EventPublisher<GreetingDelivered>>()
            val expected = Greeting(language = "fr", message = "Bonjour")
            coEvery { catalog.random() } returns expected
            val published = slot<GreetingDelivered>()
            every { events.publish(capture(published)) } returns true
            val useCase = GreetUseCase(catalog, events)

            // When
            val result = useCase()

            // Then
            result shouldBe expected
            verify { events.publish(any()) }
            published.captured.greeting shouldBe expected
        }

        test("falls back to default greeting when catalog is empty and still publishes") {
            // Given
            val catalog = mockk<GreetingCatalog>()
            val events = mockk<EventPublisher<GreetingDelivered>>()
            coEvery { catalog.random() } returns null
            val published = slot<GreetingDelivered>()
            every { events.publish(capture(published)) } returns true
            val useCase = GreetUseCase(catalog, events)

            // When
            val result = useCase()

            // Then
            result shouldBe Greeting(language = "en", message = "Hello, World!")
            verify { events.publish(any()) }
            published.captured.greeting shouldBe result
        }
    })
