package com.yonatankarp.ktor.template.adapters.output.observability

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.di.dependencies
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import java.time.Duration

fun Application.configureMetrics() {
    val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        this.registry = registry
        meterBinders =
            listOf(
                JvmMemoryMetrics(),
                JvmGcMetrics(),
                JvmThreadMetrics(),
                ProcessorMetrics(),
                ClassLoaderMetrics(),
                UptimeMetrics(),
            )
        distributionStatisticConfig =
            DistributionStatisticConfig
                .Builder()
                .percentilesHistogram(true)
                .maximumExpectedValue(Duration.ofSeconds(MAX_EXPECTED_LATENCY_SECONDS).toNanos().toDouble())
                .serviceLevelObjectives(
                    Duration.ofMillis(SLO_FAST_MS).toNanos().toDouble(),
                    Duration.ofMillis(SLO_SLOW_MS).toNanos().toDouble(),
                ).build()
    }
    dependencies {
        provide<PrometheusMeterRegistry> { registry }
        provide<MeterRegistry> { registry }
    }
}

private const val MAX_EXPECTED_LATENCY_SECONDS = 10L
private const val SLO_FAST_MS = 100L
private const val SLO_SLOW_MS = 500L
