-- Initial migration. Real domain tables come in later slices; this file exists
-- so Flyway has a baseline migration to apply on first boot and the integration
-- tests can verify the migration pipeline works end-to-end.

CREATE TABLE IF NOT EXISTS app_health (
    id         BIGSERIAL PRIMARY KEY,
    checked_at TIMESTAMP NOT NULL DEFAULT NOW()
);
