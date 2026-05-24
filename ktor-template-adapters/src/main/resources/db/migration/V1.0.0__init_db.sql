CREATE TABLE greeting (
    id       SERIAL PRIMARY KEY,
    language VARCHAR(35) NOT NULL CHECK (btrim(language) <> ''),
    message  TEXT        NOT NULL CHECK (btrim(message) <> ''),
    UNIQUE (language, message)
);

INSERT INTO greeting (language, message) VALUES
    ('en', 'Hello, World!'),
    ('es', 'Hola, Mundo!'),
    ('fr', 'Bonjour le monde!'),
    ('de', 'Hallo, Welt!'),
    ('ja', 'こんにちは世界');
