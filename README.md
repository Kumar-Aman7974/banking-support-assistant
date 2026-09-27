# AI-Powered Banking Support Assistant

A microservices-based system that uses AI (RAG) to auto-resolve
customer support tickets and routes complex issues to human agents.

## Architecture

5 services communicating via REST + Kafka:

- **service-registry** — Eureka server for service discovery
- **api-gateway** — Single entry point for all requests
- **ticket-service** — Manages support tickets (CRUD)
- **ai-service** — RAG pipeline for AI-generated resolutions
- **notification-service** — Sends email on ticket events

## Tech Stack

- Java 21, Spring Boot 4.1
- Spring Cloud (Eureka, Gateway, OpenFeign)
- PostgreSQL + pgvector (data + embeddings)
- Redis (caching)
- Apache Kafka (async events)
- Docker + Kubernetes (deployment)

## Status

🚧 In development — Day 1 of 30

## How to Run

Coming soon.