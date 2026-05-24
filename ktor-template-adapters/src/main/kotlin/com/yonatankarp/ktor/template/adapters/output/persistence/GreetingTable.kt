package com.yonatankarp.ktor.template.adapters.output.persistence

import org.jetbrains.exposed.v1.core.Table

object GreetingTable : Table("greeting") {
    val id = integer("id").autoIncrement()
    val language = varchar("language", 35)
    val message = text("message")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(language, message)
    }
}
