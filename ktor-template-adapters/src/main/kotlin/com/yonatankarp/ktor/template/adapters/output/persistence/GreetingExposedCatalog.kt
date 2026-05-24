package com.yonatankarp.ktor.template.adapters.output.persistence

import com.yonatankarp.ktor.template.application.ports.output.GreetingCatalog
import com.yonatankarp.ktor.template.domain.valueobject.Greeting
import org.jetbrains.exposed.v1.core.Random
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

class GreetingExposedCatalog : GreetingCatalog {
    override suspend fun random(): Greeting? =
        newSuspendedTransaction {
            GreetingTable
                .selectAll()
                .orderBy(Random())
                .limit(1)
                .firstOrNull()
                ?.let {
                    Greeting(
                        language = it[GreetingTable.language],
                        message = it[GreetingTable.message],
                    )
                }
        }
}
