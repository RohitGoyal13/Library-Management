# 📚 Library Management System

A production-ready **Library Management System** built with **Spring Boot + Kotlin**, featuring **JWT Authentication**, **MongoDB**, **gRPC**, and **Docker**.

---

## 🚀 Tech Stack

- **Spring Boot 3.5.x** (Kotlin)
- **MongoDB** with authentication
- **JWT** for stateless authentication
- **gRPC** for service communication
- **Docker & Docker Compose**
- **Gradle** (Kotlin DSL)

---

## ⚡ Quick Start

### Prerequisites
- Docker & Docker Compose installed
- Git

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/yourusername/library-management.git
cd library-management
```

### 2️⃣ Build the Application
```bash
./gradlew clean build
```

### 3️⃣ Run with Docker
```bash
docker-compose up --build
```

The application will start on:
- **REST API:** http://localhost:8080
- **MongoDB:** localhost:27017
- **Health Check:** http://localhost:8080/actuator/health

### 4️⃣ Test the APIs
Import the Postman collection from `postman_collection/Library Management App.postman_collection.json`

---

## 🔐 Authentication Flow

1. **Signup** - Create a new user (ADMIN or USER role)
2. **Login** - Get JWT token
3. **Use Token** - Add `Authorization: Bearer <token>` header for protected endpoints

---

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/signup?role=ADMIN` | Register admin user |
| POST | `/auth/signup?role=USER` | Register regular user |
| POST | `/auth/login` | Login and get JWT token |

**Signup/Login Request Body:**
```json
{
  "username": "arunkumar",
  "password": "987654"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### Book Management

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/books` | Add a new book | Required |

**Request Body:**
```json
{
  "title": "Ozymandious Latest Edison 2025 Part-3 Chapter-1",
  "author": "Rohit Goyal",
  "quantity": 1000
}
```

**Response:**
```json
{
  "id": "693fdb1652ce736c03d328c1",
  "title": "Ozymandious Latest Edison 2025 Part-3 Chapter-1",
  "author": "Rohit Goyal",
  "quantity": 1000
}
```

---

### Borrow & Return

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/borrow/{bookId}` | Borrow a book | Required |
| POST | `/borrow/return/{borrowId}` | Return a book | Required |
| GET | `/borrow/my` | Get my borrowed books | Required |

**Borrow Response:**
```json
{
  "id": "693fdb2525ce736c03d328c3",
  "userId": "693fdb2752ce736c03d328c2",
  "bookId": "693fdb1652ce736c03d328c1",
  "borrowedAt": "2025-12-15T09:56:34.719",
  "expiresAt": "2025-12-22T09:56:34.719",
  "policyReturnAt": "2025-12-15T22:00:00.719",
  "returned": false,
  "returnedAt": null
}
```

---

### Health Check

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Application health status |

**Response:**
```json
{
  "status": "UP"
}
```

---

## 🗄️ Database Schema

### User Collection
```json
{
  "id": "693fdb2752ce736c03d328c2",
  "username": "arunkumar",
  "password": "$2a$10$hashed_password",
  "role": "ADMIN"
}
```

### Book Collection
```json
{
  "id": "693fdb1652ce736c03d328c1",
  "title": "Ozymandious Latest Edison 2025 Part-3 Chapter-1",
  "author": "Rohit Goyal",
  "quantity": 1000
}
```

### BorrowRecord Collection
```json
{
  "id": "693fdb2525ce736c03d328c3",
  "userId": "693fdb2752ce736c03d328c2",
  "bookId": "693fdb1652ce736c03d328c1",
  "borrowedAt": "2025-12-15T09:56:34.719",
  "expiresAt": "2025-12-22T09:56:34.719",
  "policyReturnAt": "2025-12-15T22:00:00.719",
  "returned": false,
  "returnedAt": null
}
```

---

## 🐳 Docker Configuration

### Dockerfile
```dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml
```yaml
version: "3.8"

services:
  mongo:
    image: mongo:6
    container_name: lm_mongo
    restart: unless-stopped
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: root
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: lm_app
    restart: unless-stopped
    ports:
      - "8080:8080"
    depends_on:
      - mongo
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://root:root@mongo:27017/librarydb?authSource=admin
      SPRING_PROFILES_ACTIVE: docker

volumes:
  mongo_data:
```

---

## 🛠️ Configuration

### MongoDB Connection
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://root:root@mongo:27017/librarydb?authSource=admin
```

### JWT Configuration
- Token expiration: **24 hours**
- Algorithm: **HS256**
- Secret: Configured via environment variable

---

## 🏗️ Project Structure

```
library-management/
├── src/
│   ├── main/kotlin/
│   │   ├── controller/    # REST endpoints
│   │   ├── service/       # Business logic
│   │   ├── repository/    # MongoDB repositories
│   │   ├── model/         # Data models
│   │   ├── security/      # JWT & authentication
│   │   ├── grpc/          # gRPC services
│   │   └── config/        # Configuration
├── build.gradle.kts
├── Dockerfile
├── docker-compose.yml
└── postman_collection/
```

---

## 🔒 Security Features

- ✅ **JWT-based authentication** (stateless)
- ✅ **BCrypt password hashing**
- ✅ **Role-based access control** (ADMIN/USER)
- ✅ **Secured endpoints** with Bearer tokens
- ✅ **MongoDB authentication** enabled

---

## 🎯 Key Features

- **JWT Authentication** - Secure stateless authentication
- **Role Management** - ADMIN and USER roles
- **Book Management** - Add and manage books
- **Borrow System** - Track borrowed books with expiry
- **gRPC Support** - High-performance service communication
- **Dockerized** - One-command deployment
- **MongoDB** - Flexible NoSQL database
- **Health Monitoring** - Spring Boot Actuator

---

## 🔧 Useful Commands

```bash
# Stop containers
docker-compose down

# Remove volumes (clean slate)
docker-compose down -v

# View logs
docker-compose logs -f app

# Rebuild and restart
docker-compose up --build

# Build JAR only
./gradlew bootJar
```

---

## 📞 Ports

| Service | Port |
|---------|------|
| REST API | 8080 |
| MongoDB | 27017 |
| gRPC | 9090 |

---

## 👤 Author

**Rohit Goyal**  
Final Year CSE, IIIT Nagpur

📧 Email: rohit.goyal@example.com  
🔗 LinkedIn: [linkedin.com/in/rohitgoyal](https://linkedin.com/in/rohitgoyal)  
🐙 GitHub: [@rohitgoyal](https://github.com/rohitgoyal)

---

## 📝 License

MIT License - See [LICENSE](LICENSE) for details

---

<div align="center">

**Made with ❤️ using Spring Boot + Kotlin**

⭐ Star this repo if you found it useful!

</div>