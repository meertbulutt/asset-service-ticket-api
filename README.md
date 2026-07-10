# Asset Service Ticket API

Asset Service Ticket API is a Spring Boot backend project created to manage organizational assets and service tickets. The initial version includes department, user, asset, ticket and ticket comment management with validation, exception handling, PostgreSQL integration and Swagger documentation.

This repository contains the initial version prepared for internship project tracking. The project focuses on a clean backend structure, readable code and a foundation that can be improved step by step.

## Türkçe Açıklama

Asset Service Ticket API, kurum içi demirbaşların ve servis taleplerinin yönetilmesi için geliştirilmiş bir Spring Boot backend projesidir. İlk sürümde departman, kullanıcı, demirbaş, servis talebi ve talep yorumu yönetimi; validasyon, global hata yönetimi, PostgreSQL bağlantısı ve Swagger dokümantasyonu ile birlikte sunulmuştur.

Bu proje staj sürecinde GitHub üzerinden gelişimi takip edilebilir olacak şekilde hazırlanmıştır. İlk sürümün amacı sistemi gereksiz karmaşık hale getirmeden temiz, anlaşılır ve geliştirilebilir bir backend temeli oluşturmaktır.

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

- Department CRUD
- User management for domain modeling
- Asset registration and lookup by asset tag
- Service ticket creation, assignment and status updates
- Ticket comments
- Simple report summary endpoint
- DTO-based API responses
- Global exception handling
- Startup seed data for quick testing

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

PostgreSQL kullanıcı adı veya şifreniz farklıysa `src/main/resources/application.properties` dosyasındaki bağlantı bilgilerini güncelleyin.

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

Veritabanı boş olduğunda uygulama başlangıçta örnek departman, kullanıcı, demirbaş ve servis talepleri oluşturur.

## Future Improvements

- JWT authentication
- Role-based authorization
- Docker support
- Unit tests
- File attachments for tickets
- Email notifications
- Advanced filtering and search
- Cloud deployment
