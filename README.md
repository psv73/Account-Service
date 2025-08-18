# Account Service

**Account Service** is a microservice for managing user accounts, built with **Spring Boot**.  
It provides a REST API for creating, updating, retrieving, and deleting accounts, and comes with an embedded H2 database for testing and development.

## 🚀 Features
- REST API for **account management & payrolls**
- Full CRUD with **Spring Data JPA**
- **Authentication & Authorization** with Spring Security
- **Role-based access control** (Admin / User / Accountant)
- Data validation with **Hibernate Validator**
- Monitoring & metrics via **Spring Boot Actuator**
- Lightweight **H2 Database** for testing
- Easy build & run with **Gradle** (Maven-ready)

## 🛠 Technologies Used
- **Java 17+**
- **Spring Boot 4.0.0-M1**
- **Spring Web, Security, Data JPA**
- **Hibernate Validator**
- **Spring Boot Actuator**
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
1.  Clone the repository
```bash
git clone https://github.com/psv73/account-service.git
cd account-service
````
2. Run the service using Gradle:
```bash
./gradlew bootRun 
```
3. Build the JAR file:
```bash
./gradlew build
java -jar build/libs/account-service-0.0.1-SNAPSHOT.jar
```

4. After starting, the API will be available at:
```
http://localhost:28852/api  
```

## 📌 API Endpoints (Example)
| Method | Endpoint            | Description                                   |
|--------|---------------------|-----------------------------------------------|
| POST   | /auth/signup        | allows the account to register on the service |
| POST   | /auth/changepass    | changes a account password                    |
| GET    | /empl/payment       | gives access to the employee's payrolls       |
| POST   | /acct/payments      | uploads payrolls                              |   
| PUT    | /acct/payments      | updates payment information                   |   
| PUT    | /admin/account/role | changes account roles                         |
| DELETE | /admin/user/{email} | deletes a account                             |   
| GET    | /admin/user         | displays information about all users          |   
| PUT    | /admin/user/access  | lock/unlock the user                          |


## 🧪 Testing

Run tests:
```
./gradlew test 
```
## 🌟 Highlights
- Follows clean architecture & DTO pattern    
- Demonstrates secure authentication & authorization  
- Shows ability to work with databases, validation, and REST APIs 
- Production-ready practices: logging, monitoring, role management    

## 👨‍💻 About Me

Hi! My name is Sergii Ponomarenko — I am a Junior Java Developer based in Slovakia.
I am passionate about backend development, clean code, and learning modern technologies.
Currently, I am looking for part-time or full-time opportunities to grow as a Java developer.

📫 LinkedIn: [Sergii Ponomarenko](https://www.linkedin.com/in/sergii-ponomarenko-064529114/)   
💻 GitHub: [GitHub](https://github.com/psv73)
