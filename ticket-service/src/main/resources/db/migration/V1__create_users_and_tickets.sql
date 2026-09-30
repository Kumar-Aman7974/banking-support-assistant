-- Users table
CREATE TABLE users (
id BIGSERIAL PRIMARY KEY,
email VARCHAR(255) NOT NULL UNIQUE,
password_hash VARCHAR(255) NOT NULL,
full_name VARCHAR(255) NOT NULL,
role VARCHAR(50) NOT NULL,
created_At TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);


-- Tickets table
CREATE TABLE tickets (
id BIGSERIAL PRIMARY KEY,
subject VARCHAR(200) NOT NULL,
description TEXT NOT NULL,
status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    customer_id BIGINT NOT NULL,
    assigned_agent_id BIGINT,
    suggested_resolution TEXT,
    final_resolution TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    CONSTRAINT fk_tickets_customer FOREIGN KEY (customer_id) REFERENCES users(id),
CONSTRAINT fk_ticket_agent FOREIGN KEY (assigned_agent_id) REFERENCES users(id)

);


-- Indexes for common queries
CREATE INDEX idx_tickets_status ON tickets(status);
CREATE INDEX idx_tickets_customer ON tickets(customer_id);
CREATE INDEX idx_tickets_assigned_agent ON tickets(assigned_agent_id);
CREATE INDEX idx_tickets_created_at ON tickets(created_at DESC);

