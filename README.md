# AI-Powered Banking Support Assistant

A microservices-based backend system that uses AI (RAG) to auto-resolve customer support tickets and routes complex issues to human agents.

Built with Java 21, Spring Boot 3.2, and modern cloud-native patterns.

---

## Architecture
┌────────────────────┐
│ API Gateway │ (planned)
└─────────┬──────────┘
│
┌────────────────────────────┼────────────────────────────┐
│ │ │
▼ ▼ ▼
┌───────────────┐ ┌───────────────┐ ┌────────────────────┐
│ ticket-service│ │ ai-service │ │notification-service│
│ (port 8081) │ │ (planned) │ │ (planned) │
└───────┬───────┘ └───────┬───────┘ └─────────┬──────────┘
│ │ │
│ PostgreSQL │ pgvector + LLM │ Kafka
│ │ │
└──────────────────────────┴────────────────────────────┘
│
┌──────▼──────┐
│ Eureka │
│ (port 8761) │
└─────────────┘


**Service Registry** — Eureka server for service discovery
**Ticket Service** — Manages support tickets with JWT auth and role-based access
**AI Service** — RAG pipeline for AI-generated resolutions *(planned)*
**Notification Service** — Kafka-driven email notifications *(planned)*
**API Gateway** — Single entry point for all requests *(planned)*

---

## Tech Stack

| Layer | Technologies |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.2.0 |
| **Cloud** | Spring Cloud 2023.0.0 (Eureka, Gateway, OpenFeign) |
| **Database** | PostgreSQL 16 + pgvector |
| **DB Migration** | Flyway |
| **Security** | Spring Security 6 + JWT (jjwt 0.12.3) |
| **ORM** | Spring Data JPA + Hibernate |
| **Validation** | Jakarta Bean Validation |
| **Caching** | Redis *(planned)* |
| **Messaging** | Apache Kafka *(planned)* |
| **Testing** | JUnit 5, Mockito, Testcontainers *(planned)* |
| **Container** | Docker, Docker Compose |
| **Orchestration** | Kubernetes (Minikube) *(planned)* |

---

## Project Structure
banking-support-assistant/
├── docker-compose.yml # Local infrastructure (PostgreSQL + pgvector)
├── .gitignore
├── README.md
├── service-registry/ # Eureka server (port 8761)
│ ├── pom.xml
│ └── src/
└── ticket-service/ # Ticket management (port 8081)
├── pom.xml
└── src/
└── main/
├── java/com/banking/support/ticket_service/
│ ├── TicketServiceApplication.java
│ ├── controller/
│ │ ├── AuthController.java
│ │ └── TicketController.java
│ ├── dto/
│ │ ├── SignupRequest.java
│ │ ├── LoginRequest.java
│ │ ├── AuthResponse.java
│ │ ├── CreateTicketRequest.java
│ │ ├── TicketResponse.java
│ │ ├── UpdateTicketStatusRequest.java
│ │ ├── AssignAgentRequest.java
│ │ ├── AddCommentRequest.java
│ │ └── CommentResponse.java
│ ├── entity/
│ │ ├── User.java
│ │ ├── Ticket.java
│ │ └── TicketComment.java
│ ├── exception/
│ │ ├── ResourceNotFoundException.java
│ │ └── GlobalExceptionHandler.java
│ ├── repository/
│ │ ├── UserRepository.java
│ │ ├── TicketRepository.java
│ │ └── TicketCommentRepository.java
│ ├── security/
│ │ ├── JwtService.java
│ │ ├── JwtAuthenticationFilter.java
│ │ ├── CustomUserDetailsService.java
│ │ └── SecurityConfig.java
│ └── service/
│ ├── TicketService.java
│ └── AuthenticationService.java
└── resources/
├── application.yml
└── db/migration/
├── V1__create_users_and_tickets.sql
└── V2__create_ticket_comments.sql



---

## Current Status

**Phase 1 — Core Ticket Service** ✅ **Complete**

- [x] Multi-module Maven project structure
- [x] Eureka service registry
- [x] PostgreSQL + pgvector via Docker
- [x] Flyway database migrations
- [x] User, Ticket, TicketComment entities
- [x] CRUD APIs for tickets
- [x] Pagination, filtering, sorting
- [x] Comments on tickets
- [x] Status transitions and agent assignment
- [x] Global exception handling
- [x] Spring Security + JWT authentication
- [x] Role-based authorization (CUSTOMER, AGENT, ADMIN)

**Phase 2 — AI Service (RAG)** ⏳ *Planned*
**Phase 3 — Notification Service (Kafka)** ⏳ *Planned*
**Phase 4 — Redis Caching** ⏳ *Planned*
**Phase 5 — Docker + Kubernetes Deployment** ⏳ *Planned*

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- Docker Desktop
- IntelliJ IDEA (or any Java IDE)
- Postman (for API testing)

### 1. Clone the Repository


git clone https://github.com/Kumar-Aman7974/banking-support-assistant.git
cd banking-support-assistant
2. Start PostgreSQL
From project root:

bash
docker-compose up -d postgres
Verify it's running:

bash
docker ps
# Should show banking-postgres with status "Up (healthy)"
3. Build the Project
bash
cd service-registry
mvn clean install -DskipTests

cd ../ticket-service
mvn clean install -DskipTests
4. Run Services
Terminal 1 — Service Registry:

bash
cd service-registry
mvn spring-boot:run
Open http://localhost:8761 — should see Eureka dashboard.

Terminal 2 — Ticket Service:

cd ticket-service
mvn spring-boot:run
Verify TICKET-SERVICE registers with Eureka.

API Reference
Base URL: http://localhost:8081/api/v1

Authentication
Method	Endpoint	Description	Auth
POST	/auth/signup	Register new user	Public
POST	/auth/login	Login and get JWT	Public
Signup Request:

json
{
  "email": "customer@test.com",
  "password": "password123",
  "fullName": "John Customer",
  "role": "CUSTOMER"
}
Login Response:

json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "customer@test.com",
  "role": "CUSTOMER"
}

Use the token:

Authorization: Bearer <token>
Tickets
Method	Endpoint	Description	Roles
POST	/tickets	Create a ticket	CUSTOMER, ADMIN
GET	/tickets	List tickets (paginated)	Authenticated
GET	/tickets/{id}	Get ticket by ID	Authenticated
PATCH	/tickets/{id}/status	Update status	AGENT, ADMIN
PATCH	/tickets/{id}/assign	Assign agent	AGENT, ADMIN
POST	/tickets/{id}/comments	Add comment	Authenticated
GET	/tickets/{id}/comments	List comments	Authenticated
Query Parameters for GET /tickets:

Param	Default	Description
status	—	Filter by OPEN, IN_PROGRESS, RESOLVED, CLOSED
priority	—	Filter by LOW, MEDIUM, HIGH, URGENT
customerId	—	Filter by customer
page	0	Page number (0-indexed)
size	20	Items per page
sort	createdAt,desc	Sort field and direction
Example:

text
GET /tickets?status=OPEN&priority=HIGH&page=0&size=10&sort=createdAt,desc
Paginated Response:

json
{
  "content": [ ... ],
  "totalElements": 47,
  "totalPages": 5,
  "number": 0,
  "size": 10,
  "first": true,
  "last": false
}
Database Schema
users

Column	Type	Notes
id	BIGSERIAL	PK
email	VARCHAR(255)	Unique
password_hash	VARCHAR(255)	BCrypt
full_name	VARCHAR(255)	
role	VARCHAR(50)	CUSTOMER / AGENT / ADMIN
created_at, updated_at	TIMESTAMP	
tickets

Column	Type	Notes
id	BIGSERIAL	PK
subject	VARCHAR(200)	
description	TEXT	
status	VARCHAR(50)	OPEN / IN_PROGRESS / RESOLVED / CLOSED
priority	VARCHAR(50)	LOW / MEDIUM / HIGH / URGENT
customer_id	BIGINT	FK → users
assigned_agent_id	BIGINT	FK → users
suggested_resolution	TEXT	From AI service (planned)
final_resolution	TEXT	From agent
created_at, updated_at, resolved_at	TIMESTAMP	
ticket_comments

Column	Type	Notes
id	BIGSERIAL	PK
ticket_id	BIGINT	FK → tickets
author_id	BIGINT	FK → users
comment_text	TEXT	
created_at	TIMESTAMP	
Security Model
JWT-based authentication (stateless)

Tokens signed with HS256

Default expiration: 24 hours

Passwords hashed with BCrypt

Role-based access control (RBAC)

Roles
Role	Permissions
CUSTOMER	Create tickets, view own tickets, comment
AGENT	View all tickets, update status, assign, comment
ADMIN	Full access
Development Workflow
Commit Convention
This project uses Conventional Commits:


feat(scope): add new feature
fix(scope): bug fix
chore(scope): tooling, config
docs(scope): documentation
refactor(scope): code cleanup
test(scope): tests
build(scope): build config
config(scope): configuration change
Example:


feat(ticket-service): add pagination and filtering to ticket listing
Branching
main — stable, deployable

feature/* — new features (if needed later)