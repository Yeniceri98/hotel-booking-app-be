# Hotel Booking App – Backend

Backend part of a **fullstack hotel booking application** built with **Spring Boot**.  
Provides secure REST APIs for hotel, room, booking and user management.  
Uses **MySQL** as the database, **JWT** for authentication and **Swagger UI** for API documentation.

Frontend (React) is developed separately and consumes these APIs.

---

## Requirements

Ensure the following tools are installed on your system:

- Java Development Kit (JDK) 17
- Maven 3.8+
- Docker
- Git

---

## Technologies Used

### Backend
- Java 17
- Spring Boot 3.x
- Spring Web (REST APIs)
- Spring Data JPA (Hibernate)
- Spring Security
- JWT Authentication
- Bean Validation
- MySQL
- Swagger / OpenAPI
- JUnit & Mockito (Unit Testing)

### Frontend
- React (separate project)

### DevOps
- Docker (MySQL container)

---

## Database Setup (MySQL with Docker)

This project does **not** use Docker Compose.  
MySQL is started manually using `docker run`.

### Start MySQL Container

```bash
docker run -d \
  --name hotel-booking-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=hotel-booking-db \
  -e MYSQL_USER=user \
  -e MYSQL_PASSWORD=123 \
  -p 3312:3306 \
  mysql:latest
```

## Environment Variables
Set the JWT secret before running the application.

## Swagger / API Documentation
Swagger UI is enabled for API testing and exploration. It can be achieved via:
```
http://localhost:8080/swagger-ui/index.html
```

