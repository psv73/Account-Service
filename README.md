# Account Service

**Account Service** is a microservice for managing user accounts, built with **Spring Boot**.  
It provides a REST API for creating, updating, retrieving, and deleting accounts, and comes with an embedded H2 database for testing and development.

## 🚀 Features
- REST API for account management
- Full CRUD operations with **Spring Data JPA**
- Embedded **H2** database for easy development and testing
- Data validation using **Hibernate Validator**
- DTO mapping via **ModelMapper**
- Monitoring and metrics with **Spring Boot Actuator**
- Easy build and run with **Gradle** (can be adapted for Maven)

## 🛠 Technologies Used
- **Java 17+**
- **Spring Boot 4.0.0-M1**
- **Spring Web**
- **Spring Security**
- **Spring Data JPA**
- **Spring Boot Actuator**
- **Hibernate Validator**
- **H2 Database**
- **ModelMapper**
- **Google Guava**
- **Gradle**

## 📂 Project Structure
src/    
├── main/   
│ ├── java/ # Application source code   
│ └── resources/ # Configuration files and resources    
└── test/ # Unit and integration tests


## ⚙️ Build & Run
Run the service using Gradle:
```bash
./gradlew bootRun


Build the JAR file:

./gradlew build


After starting, the API will be available at:

http://localhost:8080

📌 API Endpoints (Example)
Method	Endpoint	Description
GET	/accounts	Get all accounts
GET	/accounts/{id}	Get account by ID
POST	/accounts	Create a new account
PUT	/accounts/{id}	Update an account
DELETE	/accounts/{id}	Delete an account
🧪 Testing

Run tests:

./gradlew test


💡 This project can be extended to run in Docker, integrated with CI/CD pipelines, or connected to an external database (PostgreSQL, MySQL) for production environments.
