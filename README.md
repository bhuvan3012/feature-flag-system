# 🚀 Feature Flag & Experimentation System

A production-grade **Feature Flag & Experimentation System** built with **Java 17** and **Spring Boot 3.2.5**. This system allows development and product teams to safely release features, conduct percentage-based rollouts, target specific users, and track configuration changes with detailed audit logging.

---

## 🌟 Key Features

- 🚩 **Feature Flag Lifecycle**: Create, retrieve, update, delete, and dynamically toggle feature flags in real-time.
- 🎯 **Advanced Evaluation Rules**:
  - **Global Toggle**: Enable or disable flags globally.
  - **Environment Scoping**: Isolate flags across environments (`DEV`, `STAGING`, `PROD`).
  - **User Targeting**: Target specific user IDs explicitly.
  - **Percentage Rollout**: Hash-based (MD5) deterministic rollout for smooth percentage-based canary releases.
- 🔐 **Stateless Security**: JWT-based authentication and role-based access control (`ROLE_ADMIN` vs `ROLE_USER`).
- 📜 **Audit Logging**: Comprehensive logging of administrative actions (`CREATE`, `UPDATE`, `DELETE`, `TOGGLE`) capturing timestamps and user details.
- ⚡ **Caching Support**: Integrated Spring cache configuration (`spring.cache.type=simple` / Redis support) for high-performance flag evaluation.
- 📖 **Interactive OpenAPI (Swagger)**: Fully documented REST endpoints via Swagger UI.
- 🎨 **Glassmorphic Management Dashboard**: Modern UI console for flag management and live evaluation demo playground.

---

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.5, Spring Security, Spring Data JPA, Spring Validation
- **Authentication**: JWT (JSON Web Tokens)
- **Database**: H2 (In-memory, default for zero-setup running) / MySQL (Production compatible)
- **Caching**: Spring Cache / Spring Data Redis
- **Documentation**: Springdoc OpenAPI 2.5.0
- **Frontend**: HTML5, CSS3 (Glassmorphism Dark Theme), Vanilla JavaScript, Ionicons

---

## 🚀 Quick Start & Running locally

### Prerequisites
- **JDK 17** or higher installed
- **Maven** (bundled wrapper `./mvnw` is included)

### 1. Build and Run the Application
Open a terminal in the project directory and execute:

```bash
./mvnw spring-boot:run
```

The application will start on port **`8081`**.

### 2. Default Access & Credentials
On application startup, a default admin account is automatically created if not present:

- **Username**: `admin`
- **Password**: `admin123`

---

## 🌐 Web Dashboards & UI Links

Once the server is running, access the following URLs in your browser:

- 🎛️ **Flag Management Dashboard**: [http://localhost:8081/index.html](http://localhost:8081/index.html)  
  *Log in with `admin` / `admin123` to manage feature flags, toggle states, configure percentage rollouts, and view audit history.*

- 🧪 **Interactive Evaluation Playground**: [http://localhost:8081/demo.html](http://localhost:8081/demo.html)  
  *Test how flags evaluate for different user IDs and environments in real time.*

- 📖 **Swagger API Documentation**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)  
  *Interactive REST API documentation and sandbox.*

- 🛢️ **H2 Database Console**: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)  
  *JDBC URL: `jdbc:h2:mem:feature_flags_db` (Username: `sa`, Password: leave blank)*

---

## 📡 REST API Summary

### 🔑 Auth API (`/api/v1/auth`)
- `POST /api/v1/auth/login`: Authenticate and obtain JWT token.
- `POST /api/v1/auth/register`: Register a new user.

### 🚩 Evaluation API (`/api/v1/evaluate`)
- `POST /api/v1/evaluate`: Evaluate feature flag status for a specific user and environment.
  ```json
  {
    "flagName": "new-checkout-flow",
    "userId": "user_101",
    "environment": "PROD"
  }
  ```

### ⚙️ Flags Management API (`/api/v1/flags`) — *Requires JWT Bearer Token*
- `GET /api/v1/flags`: List all feature flags.
- `GET /api/v1/flags/{name}`: Get flag details by name.
- `POST /api/v1/flags`: Create a new feature flag.
- `PUT /api/v1/flags/{name}`: Update flag configuration.
- `PATCH /api/v1/flags/{name}/toggle`: Toggle flag enabled state.
- `DELETE /api/v1/flags/{name}`: Delete a feature flag.

### 📜 Audit Logs API (`/api/v1/audit-logs`) — *Requires JWT Bearer Token*
- `GET /api/v1/audit-logs`: Get all audit logs.
- `GET /api/v1/audit-logs/{flagName}`: Get audit logs for a specific flag.

---

## 🧪 Running Unit Tests

Run the full automated unit test suite with:

```bash
./mvnw clean test
```
