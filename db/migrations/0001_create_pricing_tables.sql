-- 0001_create_pricing_tables.sql
-- PostgreSQL migration: initial pricing & inventory tables

CREATE TABLE IF NOT EXISTS shops (
  id BIGSERIAL PRIMARY KEY,
  external_shop_id TEXT,
  name TEXT,
  city TEXT,
  location GEOGRAPHY(POINT),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS models (
  id BIGSERIAL PRIMARY KEY,
  external_model_id TEXT,
  name TEXT,
  capacity INT,
  class TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS vehicles (
  id BIGSERIAL PRIMARY KEY,
  model_id BIGINT REFERENCES models(id),
  shop_id BIGINT REFERENCES shops(id),
  external_vehicle_id TEXT,
  status TEXT,
  current_price NUMERIC(10,2),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_vehicles_model_shop ON vehicles(model_id, shop_id);

CREATE TABLE IF NOT EXISTS competitor_prices (
  id BIGSERIAL PRIMARY KEY,
  model_id BIGINT REFERENCES models(id),
  shop_id BIGINT REFERENCES shops(id),
  source TEXT,
  price NUMERIC(10,2),
  collected_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_competitor_prices_model_city_collected ON competitor_prices(model_id, shop_id, collected_at);

CREATE TABLE IF NOT EXISTS price_history (
  id BIGSERIAL PRIMARY KEY,
  vehicle_id BIGINT REFERENCES vehicles(id),
  model_id BIGINT REFERENCES models(id),
  old_price NUMERIC(10,2),
  new_price NUMERIC(10,2),
  reason TEXT,
  trigger_source TEXT,
  operator TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS pricing_rules (
  id BIGSERIAL PRIMARY KEY,
  scope TEXT, -- global/shop/model
  target_id BIGINT, -- nullable: shop id or model id
  type TEXT,
  params JSONB,
  active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS inventory_events (
  id BIGSERIAL PRIMARY KEY,
  vehicle_id BIGINT REFERENCES vehicles(id),
  event_type TEXT,
  qty INT,
  occurred_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Sample seed (commented)
-- INSERT INTO shops (external_shop_id, name, city) VALUES ('shop-1001','示例门店','Beijing');
