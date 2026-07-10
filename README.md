# Asset Service Ticket API

Asset Service Ticket API is a RESTful backend application for managing company assets and service tickets. It provides endpoints for departments, users, assets, service tickets, ticket comments and a basic report summary.

The project is built with Spring Boot and follows a layered architecture using controllers, services, repositories, entities and DTOs. It includes request validation, global exception handling, PostgreSQL integration, seed data and Swagger/OpenAPI documentation.

## Technologies

- Java 21
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Data JPA
- PostgreSQL
- Lombok
- Bean Validation
- Swagger / OpenAPI

## Features

- Department, user and asset management
- Asset lookup by asset tag
- Service ticket creation and updates
- Technician assignment for tickets
- Ticket status management
- Ticket comments
- Basic report summary
- DTO-based request and response models
- Validation and centralized error responses
- Startup seed data for local testing

## API Endpoints

### Departments

- `POST /api/departments`
- `GET /api/departments`
- `GET /api/departments/{id}`
- `PUT /api/departments/{id}`
- `DELETE /api/departments/{id}`

### Users

- `POST /api/users`
- `GET /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `PATCH /api/users/{id}/deactivate`

### Assets

- `POST /api/assets`
- `GET /api/assets`
- `GET /api/assets?type=LAPTOP&status=ACTIVE`
- `GET /api/assets/{id}`
- `GET /api/assets/by-tag/{assetTag}`
- `PUT /api/assets/{id}`
- `DELETE /api/assets/{id}`

### Tickets

- `POST /api/tickets`
- `GET /api/tickets`
- `GET /api/tickets/{id}`
- `PUT /api/tickets/{id}`
- `PATCH /api/tickets/{id}/assign`
- `PATCH /api/tickets/{id}/status`
- `DELETE /api/tickets/{id}`

### Ticket Comments

- `POST /api/tickets/{ticketId}/comments`
- `GET /api/tickets/{ticketId}/comments`

### Reports

- `GET /api/reports/summary`

## Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE asset_ticket_db;
```

Default local configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/asset_ticket_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

If your PostgreSQL username or password is different, update `src/main/resources/application.properties`.

## How to Run

```bash
mvn spring-boot:run
```

The API will run at:

```text
http://localhost:8080
```

## Swagger Documentation

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

## Seed Data

When the database is empty, the application creates demo departments, users, assets and tickets on startup.

## Future Improvements

- JWT authentication
- Role-based authorization
- Docker support
- Unit tests
- File attachments for tickets
- Email notifications
- Advanced filtering and search
- Cloud deployment
