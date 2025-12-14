# 📚 Library Management System (Spring Boot + Kotlin)

A **production-ready Library Management System** built using **Spring Boot (Kotlin)** with **MongoDB**, **JWT-based authentication**, **Role-based security**, **gRPC communication**, and **Dockerized deployment**.

---

## 🚀 Tech Stack

### Backend
- **Spring Boot 3.5.x**
- **Kotlin (JVM)**
- **MongoDB**
- **Spring Security**
- **JWT Authentication (jjwt)**

### Communication
- **gRPC (Java-based stubs)**
- **Protocol Buffers**

### DevOps
- **Docker**
- **Docker Compose**
- **Gradle (Kotlin DSL)**

---

## 🧱 Architecture Overview

```
library-management
├── controller        # REST APIs
├── grpc              # gRPC services
├── service           # Business logic
├── repository        # MongoDB repositories
├── security          # JWT, filters, security config
├── model             # Domain models
├── proto             # gRPC .proto definitions
└── config            # App & security configs
```

### Core Features
- **REST APIs** for client-facing operations
- **gRPC** for internal/service-to-service communication
- **JWT** for stateless authentication
- **Role-based access control** (`ADMIN`, `USER`)

---

## 🔐 Authentication & Authorization

### JWT-based Authentication
- **Stateless security** (no server session)
- Token contains:
  - `username`
  - `role`
  - `expiration`
- Spring Security filter validates JWT on every request

### Security Flow
```
1. User logs in with credentials
2. Server validates & generates JWT
3. Client stores JWT (localStorage/cookie)
4. Client sends JWT in Authorization header
5. JwtAuthenticationFilter validates token
6. Request proceeds to controller
```

### Roles & Permissions

| Role  | Permissions |
|-------|-------------|
| **ADMIN** | Manage books, users, view all borrows, approve/reject requests |
| **USER**  | View books, borrow/return own books, view own history |

### Security Configuration
- Password encryption using **BCrypt**
- CORS enabled for cross-origin requests
- CSRF disabled (stateless JWT approach)
- H2 console and health endpoints publicly accessible

---

## 🔁 gRPC Integration

### Why gRPC?
- **High-performance** binary protocol
- **Type-safe** service definitions
- **Bi-directional streaming** support
- **Language-agnostic** (polyglot microservices)

### Implementation Details
- gRPC server integrated using `grpc-server-spring-boot-starter`
- Java-based gRPC stubs (stable & production-safe)
- Protocol Buffers for schema definition
- Running on port **9090**

### Available gRPC Services

#### LibraryService
```protobuf
service LibraryService {
  rpc Ping(PingRequest) returns (PingResponse);
  rpc GetBookById(BookRequest) returns (BookResponse);
  rpc ListAllBooks(Empty) returns (BookListResponse);
}
```

#### Example: Health Check
```kotlin
override fun ping(request: PingRequest, responseObserver: StreamObserver<PingResponse>) {
    val response = PingResponse.newBuilder()
        .setMessage("Library Service is running!")
        .build()
    responseObserver.onNext(response)
    responseObserver.onCompleted()
}
```

---

## 🗄️ Database

### MongoDB Schema Design

#### User Collection
```json
{
  "_id": "ObjectId",
  "username": "string",
  "password": "string (BCrypt hashed)",
  "role": "ADMIN | USER",
  "email": "string",
  "createdAt": "timestamp"
}
```

#### Book Collection
```json
{
  "_id": "ObjectId",
  "title": "string",
  "author": "string",
  "isbn": "string",
  "totalCopies": "number",
  "availableCopies": "number",
  "category": "string",
  "publishedYear": "number"
}
```

#### BorrowRecord Collection
```json
{
  "_id": "ObjectId",
  "userId": "string",
  "bookId": "string",
  "borrowDate": "timestamp",
  "dueDate": "timestamp",
  "returnDate": "timestamp | null",
  "status": "BORROWED | RETURNED | OVERDUE"
}
```

### Repository Layer
- Spring Data MongoDB repositories
- Custom queries using `@Query` annotations
- Pagination & sorting support
- Transaction support for critical operations

---

## 📡 REST API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login and get JWT | No |
| POST | `/api/auth/refresh` | Refresh JWT token | Yes |

#### Example: Login Request
```json
POST /api/auth/login
{
  "username": "john_doe",
  "password": "password123"
}
```

#### Example: Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "role": "USER"
}
```

### Book Management Endpoints

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/books` | Get all books | USER, ADMIN |
| GET | `/api/books/{id}` | Get book by ID | USER, ADMIN |
| POST | `/api/books` | Add new book | ADMIN |
| PUT | `/api/books/{id}` | Update book | ADMIN |
| DELETE | `/api/books/{id}` | Delete book | ADMIN |
| GET | `/api/books/search?query={q}` | Search books | USER, ADMIN |

#### Example: Add Book Request
```json
POST /api/books
Authorization: Bearer <jwt_token>
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "totalCopies": 5,
  "category": "Software Engineering",
  "publishedYear": 2008
}
```

### Borrow Management Endpoints

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/borrows/borrow` | Borrow a book | USER, ADMIN |
| POST | `/api/borrows/return/{id}` | Return a book | USER, ADMIN |
| GET | `/api/borrows/my-borrows` | Get user's borrows | USER, ADMIN |
| GET | `/api/borrows/all` | Get all borrows | ADMIN |
| GET | `/api/borrows/overdue` | Get overdue books | ADMIN |

#### Example: Borrow Book Request
```json
POST /api/borrows/borrow
Authorization: Bearer <jwt_token>
{
  "bookId": "507f1f77bcf86cd799439011",
  "durationDays": 14
}
```

### User Management Endpoints (ADMIN Only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

---

## 🐳 Docker Setup

### Docker Compose Configuration

```yaml
version: '3.8'

services:
  lm_mongo:
    image: mongo:7.0
    container_name: lm_mongo
    environment:
      MONGO_INITDB_DATABASE: library
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db
    networks:
      - library_network

  lm_app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: lm_app
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATA_MONGODB_URI: mongodb://lm_mongo:27017/library
      JWT_SECRET: ${JWT_SECRET:-your-super-secret-jwt-key-change-in-production}
    ports:
      - "8080:8080"
      - "9090:9090"
    depends_on:
      - lm_mongo
    networks:
      - library_network

volumes:
  mongo_data:

networks:
  library_network:
    driver: bridge
```

### Dockerfile

```dockerfile
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Running the Application

```bash
# Start all services
docker-compose up --build

# Run in detached mode
docker-compose up -d

# View logs
docker-compose logs -f lm_app

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

---

## ⚙️ Configuration

### Application Profiles

#### application.yml (Default)
```yaml
spring:
  application:
    name: library-management
  data:
    mongodb:
      uri: mongodb://localhost:27017/library
      auto-index-creation: true

grpc:
  server:
    port: 9090

jwt:
  secret: ${JWT_SECRET:default-secret-key-change-me}
  expiration: 86400000 # 24 hours in milliseconds

server:
  port: 8080
  error:
    include-message: always
```

#### application-docker.yml
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://lm_mongo:27017/library

logging:
  level:
    org.springframework: INFO
    com.library: DEBUG
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active profile | `default` |
| `SPRING_DATA_MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/library` |
| `JWT_SECRET` | JWT signing key | (required in production) |
| `JWT_EXPIRATION` | Token expiration (ms) | `86400000` (24h) |
| `GRPC_SERVER_PORT` | gRPC server port | `9090` |

---

## 🧪 Testing

### Test Structure
```
src/test/kotlin
├── controller     # REST API integration tests
├── service        # Business logic unit tests
├── security       # JWT & authentication tests
└── grpc           # gRPC service tests
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests LibraryServiceTest

# Run with coverage
./gradlew test jacocoTestReport
```

### Example Test Cases

#### Unit Test Example
```kotlin
@Test
fun `should successfully borrow available book`() {
    val book = Book(
        id = "1",
        title = "Test Book",
        availableCopies = 5
    )
    whenever(bookRepository.findById("1")).thenReturn(Optional.of(book))
    
    val result = borrowService.borrowBook("1", "user123")
    
    assertThat(result.status).isEqualTo(BorrowStatus.BORROWED)
    verify(bookRepository).save(any())
}
```

#### Integration Test Example
```kotlin
@Test
fun `should return 401 when accessing protected endpoint without token`() {
    mockMvc.perform(get("/api/books"))
        .andExpect(status().isUnauthorized)
}
```

---

## 📦 Build & Deployment

### Local Build

```bash
# Clean build
./gradlew clean build

# Skip tests
./gradlew clean build -x test

# Generate JAR
./gradlew bootJar
```

### Generated Artifacts
```
build/libs/library-management-0.0.1-SNAPSHOT.jar
```

### Running Locally

```bash
# Using Gradle
./gradlew bootRun

# Using JAR
java -jar build/libs/library-management-0.0.1-SNAPSHOT.jar

# With profile
java -jar build/libs/library-management-0.0.1-SNAPSHOT.jar --spring.profiles.active=docker
```

---

## 🔌 Service Ports

| Service | Port | Protocol | Description |
|---------|------|----------|-------------|
| REST API | 8080 | HTTP | Main REST endpoints |
| gRPC Server | 9090 | gRPC | Internal service communication |
| MongoDB | 27017 | TCP | Database |
| Actuator | 8080/actuator | HTTP | Health & metrics |

---

## 🛡️ Production Best Practices Implemented

### Security
- ✅ JWT-based stateless authentication
- ✅ BCrypt password hashing
- ✅ Role-based authorization
- ✅ CORS configuration
- ✅ Input validation & sanitization
- ✅ SQL injection prevention (NoSQL)

### Architecture
- ✅ Clean layered architecture (Controller → Service → Repository)
- ✅ Separation of concerns
- ✅ Dependency injection
- ✅ Interface-based design

### Performance
- ✅ Database indexing
- ✅ Connection pooling
- ✅ Pagination for large datasets
- ✅ Efficient query design

### Monitoring
- ✅ Spring Boot Actuator enabled
- ✅ Health check endpoints
- ✅ Structured logging
- ✅ gRPC health service

### DevOps
- ✅ Dockerized deployment
- ✅ Multi-stage Docker builds
- ✅ Environment-based configuration
- ✅ Volume persistence

### Code Quality
- ✅ Kotlin DSL for type-safe builds
- ✅ Comprehensive error handling
- ✅ Unit & integration tests
- ✅ Clean code principles

---

## 📈 Future Enhancements

### Short-term
- [ ] Redis caching layer
- [ ] API rate limiting
- [ ] Email notifications
- [ ] Book reservation system
- [ ] Advanced search filters

### Mid-term
- [ ] gRPC client implementation
- [ ] Kafka event streaming
- [ ] Elasticsearch integration
- [ ] GraphQL API
- [ ] Admin dashboard UI

### Long-term
- [ ] Microservices architecture
- [ ] Kubernetes deployment
- [ ] Service mesh (Istio)
- [ ] Centralized logging (ELK stack)
- [ ] Prometheus & Grafana monitoring
- [ ] CI/CD pipeline (Jenkins/GitHub Actions)

---

## 🐛 Troubleshooting

### Common Issues

#### MongoDB Connection Failed
```bash
# Check if MongoDB is running
docker ps | grep mongo

# Check logs
docker logs lm_mongo

# Restart MongoDB
docker-compose restart lm_mongo
```

#### JWT Token Invalid
```bash
# Verify JWT_SECRET is set
echo $JWT_SECRET

# Generate new secret (Linux/Mac)
openssl rand -base64 32

# Windows PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

#### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Or change port in application.yml
server.port=8081
```

---

## 📚 API Documentation

### Postman Collection
Import the provided Postman collection for testing:
```
docs/Library-Management-API.postman_collection.json
```

### Swagger UI (Optional)
If enabled, access at: `http://localhost:8080/swagger-ui.html`

---

## 🤝 Contributing

```bash
# Fork the repository
# Create feature branch
git checkout -b feature/amazing-feature

# Commit changes
git commit -m "Add amazing feature"

# Push to branch
git push origin feature/amazing-feature

# Open Pull Request
```

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Rohit Goyal**  
Final Year CSE, IIIT Nagpur

📧 Email: rohit.goyal@example.com  
🔗 LinkedIn: [linkedin.com/in/rohitgoyal](https://linkedin.com/in/rohitgoyal)  
🐙 GitHub: [@rohitgoyal](https://github.com/rohitgoyal)

---

## ⭐ Acknowledgments

- Spring Boot Team for the amazing framework
- MongoDB for flexible data modeling
- gRPC community for high-performance RPC
- Docker for seamless containerization

---

## 🎯 Project Status

**Status:** ✅ Production Ready  
**Version:** 1.0.0  
**Last Updated:** December 2024

---

## 📞 Support

For support and queries:
- 📧 Email: support@librarymanagement.com
- 💬 Discord: [Join Server](https://discord.gg/library)
- 📖 Wiki: [Documentation](https://github.com/rohitgoyal/library-management/wiki)

---

<div align="center">

### ⭐ If you found this project useful, please give it a star!

**Made with ❤️ by Rohit Goyal**

</div>