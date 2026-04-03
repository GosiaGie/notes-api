# notes API

notes API is a backend application based on **REST API architecture**. 
It is designed for comprehensive note management, including creation, editing, viewing, as well as sharing and access control.

## 1. Key Features:

#### 1. SECURITY
* **Multi-level Authorization**: Protected endpoints require a **JWT token**. This decision was made to ensure scalability and avoid server-side session overhead. Access to specific resources (notes) is also verified against the user's role and ownership. 
* **Rate Limiting**: Implemented for the `/login` endpoint to prevent brute-force attacks (returns `429 Too Many Requests`).

#### 2. AUDIT & HISTORY
* **Hibernate Envers**: Fully tracks the history of note changes (versioning).
* **Custom UserRevisionListener**: Each note modification is explicitly linked to the specific user who performed the action.

#### 3. DATA CONSISTENCY
* **Optimistic Locking**: Using the `@Version` field to protect data from concurrent modifications by multiple users.
* **Flushing Strategy**: Forced synchronization (`flush`) ensures that the client receives the most up-to-date version of the resource (verified via integration tests).

#### 4. ARCHITECTURE
* **Layered Architecture**: Clear separation between **Controller -> Service -> Repository** layers. This decouples HTTP infrastructure from business logic and data access.

  - Controller Layer: Handles incoming HTTP requests and maps Data Transfer Objects (DTOs).
  - Service Layer: Encapsulates the core business logic, remaining decoupled from database-specific details.
  - Repository Layer: Leverages Spring Data JPA to provide a clean abstraction for database operations.

* **DTO Pattern (Request/Response)**: For enhanced security and API contract clarity, Data Transfer Objects are used to avoid exposing internal database entities.
* **UserPrincipal**: Custom implementation for easy and secure access to the authenticated user's ID across the application.

#### 5. TESTING
* **Unit Testing**: Developed using **JUnit** and **Mockito** for dependency mocking.
* **Integration Testing**: Powered by **Testcontainers (MySQL)**, guaranteeing a test environment identical to the production setup.

## 2. Getting Started (Step-by-Step)

### Prerequisites:
- Java 21 & Maven installed.
- Docker Desktop (installed and running).

1. **Build the JAR file:**
   ```powershell
   mvn clean package -DskipTests
2. **Launch the containers:**
   ```powershell
   docker-compose up --build

## 3. Configuration Parameters
The application can be configured via `application.properties` or environment variables:
| Parameter | Description | Default Value |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | JDBC URL for the MySQL database | `jdbc:mysql://db:3306/notesdb` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | `rootpassword` |
| `JWT_SECRET_KEY` | Secret key used for signing tokens | `ilovecatsanddogs` |
| `spring.jpa.hibernate.ddl-auto` | Hibernate schema management | `update` |


