# Student Registration & Management System

A web application to register students through an HTML form, store them in PostgreSQL using plain JDBC, and list them on a separate page. Includes the bonus feature to view one student by ID.

## Technologies Used
- Java 17
- Spring Boot 3 / Spring MVC
- Thymeleaf
- PostgreSQL + JDBC (`Connection`, `PreparedStatement`, `ResultSet`)
- Maven
- No Spring Data JPA / Hibernate

## Project Structure
```
student-management/
├── pom.xml
├── sql/schema.sql
├── screenshots/
└── src/main/
    ├── java/com/school/studentmanagement/
    │   ├── StudentManagementApplication.java
    │   ├── config/DatabaseConfig.java
    │   ├── controller/StudentController.java
    │   ├── exception/ (DatabaseException, DuplicateEmailException)
    │   ├── model/Student.java
    │   ├── repository/StudentRepository.java
    │   └── service/StudentService.java
    └── resources/
        ├── application.properties
        ├── static/css/style.css
        └── templates/ (register.html, students.html)
```

## Database Setup
```sql
CREATE DATABASE student_management;
\c student_management

CREATE TABLE students (
    id        SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    age       INTEGER CHECK (age > 18),
    email     VARCHAR(150) UNIQUE NOT NULL
);
```
The form collects `firstName` and `lastName` separately; the repository combines them into `full_name` before the INSERT, and splits them again when reading.

## Running the Application
1. Install JDK 17+, Maven and PostgreSQL. Create the database and table (above).
2. Set the credentials as environment variables (nothing is hard-coded in Git):
   - Linux/Mac: `export DB_USERNAME=postgres DB_PASSWORD=yourpassword`
   - Windows (PowerShell): `$env:DB_USERNAME="postgres"; $env:DB_PASSWORD="yourpassword"`
   - Optional: `DB_URL` (default `jdbc:postgresql://localhost:5432/student_management`)
3. Run: `mvn spring-boot:run`
4. Open http://localhost:8080/student/register

## IoC, Bean Creation and Dependency Injection
- **IoC (Inversion of Control):** we do not create objects with `new`. The Spring IoC container creates them and controls their lifecycle.
- **Bean creation:** at startup, component scanning finds `@Controller` (`StudentController`), `@Service` (`StudentService`), `@Repository` (`StudentRepository`) and `@Configuration` (`DatabaseConfig`, which defines the `DataSource` bean with `@Bean`). Each becomes a Spring-managed bean.
- **Constructor-based DI:** Spring sees the constructors and injects dependencies automatically:
  - `DataSource` → `StudentRepository`
  - `StudentRepository` → `StudentService`
  - `StudentService` → `StudentController`
- The container creates beans in dependency order: DataSource → Repository → Service → Controller. No class uses `new` for another layer.

## Controller Handler Mappings
`StudentController` uses `@Controller` and `@RequestMapping("/student")` as the common path.

| Method | Endpoint | Annotation | Returns |
|---|---|---|---|
| GET | `/student/register` | `@GetMapping("/register")` | `register.html` (empty form) |
| POST | `/student/register` | `@PostMapping("/register")` + `@ModelAttribute` | saves student, returns `register.html` with success message |
| GET | `/student/all` | `@GetMapping("/all")` | `students.html` (all students) |
| GET | `/student/{id}` (bonus) | `@GetMapping("/{id}")` + `@PathVariable` | `students.html` showing one student, or a "not found" message |

`Model` passes data (student object, list, messages) to the Thymeleaf views.

## Screenshots
**Registration page**
![Registration](screenshots/register.png)

**Student list**
![List](screenshots/students.png)

**Saved database records**
![Database](screenshots/database.png)
