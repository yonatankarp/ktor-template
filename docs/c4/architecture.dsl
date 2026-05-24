workspace {
    model {
        user = person "User"

        softwareSystem = softwareSystem "ktor-template" "A Ktor 3.x HTTP service template demonstrating hexagonal architecture, observability, and Postgres-backed persistence." {

            service = container "Ktor Service" "A Kotlin/JVM HTTP service serving the demo greetings endpoint." "Kotlin, Ktor, JVM" {
                healthRoutes      = component "HealthHttpAdapter"      "Exposes GET /health for k8s liveness/readiness."
                metricsRoutes     = component "MetricsHttpAdapter"     "Exposes GET /metrics in Prometheus exposition format."
                greetingsRoutes   = component "GreetingsHttpAdapter"   "Exposes GET /greetings/random; HTTP layer of the demo feature."
                greetUseCase      = component "GreetUseCase"           "Application port impl — pulls a random greeting from the catalog and publishes a GreetingDelivered event."
                greetingCatalog   = component "GreetingExposedCatalog" "Output adapter implementing the GreetingCatalog port against Postgres via Exposed."
                eventBus          = component "InMemoryEventBus"       "Output adapter implementing EventPublisher; in-process SharedFlow."
                greetingLogger    = component "GreetingDeliveredLogger" "Subscriber to the event bus; logs GreetingDelivered events."

                greetingsRoutes -> greetUseCase    "invokes"
                greetUseCase    -> greetingCatalog "random()"
                greetUseCase    -> eventBus        "publish(GreetingDelivered)"
                eventBus        -> greetingLogger  "emits"
            }

            postgres = container "Postgres" "Application-owned relational database; Flyway migrations on app startup." "PostgreSQL 18" "Database"

            greetingCatalog -> postgres "SELECT random greeting" "JDBC / Exposed"
        }

        user -> softwareSystem      "Uses"
        user -> service             "Hits HTTP endpoints" "HTTPS / JSON"
    }

    views {
        systemContext softwareSystem {
            include *
            autolayout lr
        }
        container softwareSystem {
            include *
            autoLayout lr
        }
        component service {
            include *
            autoLayout
        }

        styles {
            element "Person" {
                color #ffffff
                fontSize 22
                shape Person
                background #08427b
            }
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "Database" {
                shape Cylinder
            }
            element "Component" {
                background #85bbf0
                color #000000
            }
        }
    }
}
