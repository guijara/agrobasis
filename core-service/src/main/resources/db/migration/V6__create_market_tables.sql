CREATE TABLE market_quote (
       id UUID PRIMARY KEY,
       commodity VARCHAR(50) NOT NULL,
       source VARCHAR(255) NOT NULL,
       price NUMERIC(12, 2) NOT NULL,
       currency VARCHAR(3) NOT NULL,
       unit VARCHAR(50) NOT NULL,
       quoted_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_market_quote_commodity ON market_quote(commodity);
CREATE INDEX idx_market_quote_quoted_at ON market_quote(quoted_at);

CREATE TABLE exchange_rate (
       id UUID PRIMARY KEY,
       from_currency VARCHAR(3) NOT NULL,
       to_currency VARCHAR(3) NOT NULL,
       rate NUMERIC(12, 6) NOT NULL,
       source VARCHAR(255) NOT NULL,
       quoted_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_exchange_rate_pair ON exchange_rate(from_currency, to_currency);
CREATE INDEX idx_exchange_rate_quoted_at ON exchange_rate(quoted_at);
