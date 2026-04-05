# Finance Access Backend

## 1. Project Overview

This project is a **Finance Data Processing and Access Control** backend built with Spring Boot. It supports a finance dashboard where users see and manage financial records based on their **role**. The focus is on **correct backend logic**, **JWT security**, and **role-based access**—not on extra layers or tools that do not add clear value for the assignment.

---

## 2. Features

- User **registration** and **login**
- **JWT** authentication for protected APIs
- **Role-based access control** (VIEWER, ANALYST, ADMIN)
- **User management** (admin only): list users, change role, change status
- **Financial records** CRUD (create, read, update, delete) with rules per role
- **Filtering** records by type, category, and date range
- **Dashboard** APIs: totals, category breakdown, monthly trend, recent activity
- **Input validation** and a **global exception handler** with clear HTTP status codes
- **MySQL** persistence with Spring Data JPA
- **Sample users and records** seeded at startup for easy testing

---

## 3. Tech Stack

| Area | Technology |
|------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3 |
| Security | Spring Security + JWT |
| Data | Spring Data JPA (Hibernate) |
| Database | MySQL |
| Build | Maven |
| Validation | Bean Validation (`@Valid`, etc.) |

---

## 4. Project Structure

Main Java code lives under (this matches the folders in your IDE):

`src/main/java/com/example/financebackend/`

Package name: **`com.example.financebackend`**

Rough layout:

```
src/main/java/com/example/financebackend/
├── FinanceAccessBackendApplication.java   # Spring Boot entry point
├── config/
│   ├── DataSeeder.java                    # Sample users + records on startup
│   └── SecurityConfig.java                # Security rules + JWT filter order
├── controller/                            # REST controllers (auth, users, records, dashboard)
├── dto/
│   ├── auth/
│   ├── dashboard/
│   ├── records/
│   └── users/
├── entity/                                # JPA entities (User, FinancialRecord)
├── enums/                                 # Role, UserStatus, RecordType
├── exception/                             # Global handler + API error DTOs
├── repository/                            # Spring Data JPA repositories
├── security/                              # JWT util, filter, UserDetails
└── service/                               # Business logic
```

Resources:

- `src/main/resources/application.properties` — server port, database URL, JWT settings

---

## 5. Database Design

Database name used in this project (must match `spring.datasource.url` in `application.properties`): **`finance_access_backend`**

**Tables (created/updated by JPA `ddl-auto`):**

1. **`app_users`** (entity: `User`)
   - `id`, `name`, `email` (unique), `password` (hashed), `role`, `status`

2. **`financial_records`** (entity: `FinancialRecord`)
   - `id`, `amount`, `type` (INCOME / EXPENSE), `category`, `date`, `description`
   - `created_by_user_id` (optional link to user who created the row)
   - `created_at` (for recent activity)

Relationships are kept simple: a record can point to the user who created it.

---

## 6. Roles and Permissions

| Role | What they can do |
|------|------------------|
| **VIEWER** | Only **dashboard** APIs. Cannot read or change financial records through the record APIs. |
| **ANALYST** | **Read** financial records and use **all dashboard** APIs. Cannot create/update/delete records or manage users. |
| **ADMIN** | **Full access**: manage users (list, role, status) and **full CRUD** on financial records. |

**Note:** Protected routes need a valid **JWT** in the `Authorization` header. Without a token (or with a bad/expired token), the API returns **401**. If the token is valid but the role is not allowed, the API returns **403**.

---

## 7. API Endpoints

**Base URL (default):** `http://localhost:8080`

### Auth (no JWT required)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/register` | Register a new user (default role is VIEWER) |
| POST | `/api/auth/login` | Login; returns a JWT `token` |

### Users (ADMIN only — JWT required)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get one user |
| PUT | `/api/users/{id}/role` | Change user role (body: `{"role":"VIEWER"}` etc.) |
| PUT | `/api/users/{id}/status` | Change status (body: `{"status":"ACTIVE"}` or `INACTIVE`) |

### Records (JWT required — rules depend on role)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/records` | Create record (**ADMIN** only) |
| GET | `/api/records` | List records (with optional filters — **ANALYST** / **ADMIN**) |
| GET | `/api/records/{id}` | Get one record (**ANALYST** / **ADMIN**) |
| PUT | `/api/records/{id}` | Update record (**ADMIN** only) |
| DELETE | `/api/records/{id}` | Delete record (**ADMIN** only) |

### Filtering (same GET as list, query params)

Examples:

- `GET /api/records?type=EXPENSE`
- `GET /api/records?category=Food`
- `GET /api/records?startDate=2026-01-01&endDate=2026-03-31`

You can combine filters where it makes sense.

### Dashboard (VIEWER / ANALYST / ADMIN — JWT required)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/dashboard/summary` | Total income, total expenses, net balance |
| GET | `/api/dashboard/category-summary` | Totals per category |
| GET | `/api/dashboard/monthly-trend` | Monthly trend (last 6 months in the current implementation) |
| GET | `/api/dashboard/recent-activity` | Latest records by creation time |

---

## 8. Setup and Run Instructions

### Prerequisites

- JDK 17
- Maven
- MySQL Server running locally

### Step 1: Create the database

In MySQL (Workbench or command line), run:

```sql
CREATE DATABASE finance_access_backend;
```

### Step 2: Configure the application

Open `src/main/resources/application.properties` and set:

- **Database URL** — use the same database name as above, for example:

  `jdbc:mysql://localhost:3306/finance_access_backend?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`

- **Username and password** for your MySQL user
- **`app.jwt.secret`** — use a long random string (at least about 32 characters) for real use

### Step 3: Run the project

From the project root folder:

```bash
mvn spring-boot:run
```

Wait until you see that Tomcat started on port **8080** (or the port you set).

**If port 8080 is busy:** stop the other process using that port, or change `server.port` in `application.properties`.

---

## 9. Database Configuration (summary)

| Setting | Purpose |
|---------|---------|
| `spring.datasource.url` | MySQL host, port, database name |
| `spring.datasource.username` / `password` | MySQL login |
| `spring.jpa.hibernate.ddl-auto` | `update` updates tables automatically (good for local / assignment work) |
| `app.jwt.secret` | Secret key to sign JWTs |
| `app.jwt.expiration-ms` | Token lifetime in milliseconds |

---

## 10. Seed Users

After the app starts successfully, these users are created (if not already present):

| Email | Password | Role |
|-------|----------|------|
| admin@demo.com | password123 | ADMIN |
| analyst@demo.com | password123 | ANALYST |
| viewer@demo.com | password123 | VIEWER |

Use them to test different permission levels.

---

## 11. Sample API Testing Flow

1. **Login**  
   - `POST /api/auth/login`  
   - Body (JSON): `email`, `password`  
   - Copy the `token` from the response.

2. **Call protected APIs**  
   - Add header: `Authorization: Bearer <paste token here>`  
   - Try `GET /api/dashboard/summary` with **viewer** token — should work.  
   - Try `GET /api/records` with **viewer** token — should be **forbidden** for this project’s rules.  
   - Try `GET /api/records` with **analyst** token — should work.  
   - Try `GET /api/users` with **admin** token — should work; with **analyst** — should be **forbidden**.

3. **Postman / PowerShell**  
   - Use **POST** for login with a JSON body. Opening the login URL in a browser only sends **GET**, which is not how login is meant to be called.

Example PowerShell login:

```powershell
$body = @{ email = "admin@demo.com"; password = "password123" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $body -ContentType "application/json"
```

---

## 12. Validation and Error Handling

- Request bodies use validation annotations (e.g. non-blank fields, email format, amount &gt; 0).
- A **global exception handler** returns JSON errors with a clear **HTTP status**:
  - **400** — bad input / validation
  - **401** — not logged in or bad credentials
  - **403** — logged in but not allowed for this action
  - **404** — user or record not found
  - **409** — duplicate email on register
  - **500** — unexpected server error (message kept simple on purpose)

---

## 13. Assumptions Made

- MySQL is installed and running on the same machine as the app.
- Users create the database name that matches `application.properties` (default in this repo: **`finance_access_backend`**) and set username/password there.
- New registrations get a default role of **VIEWER** unless an admin changes it later.
- Dashboard totals are computed from the data stored in the database for this assignment (no external finance system).
- Monthly trend uses a **fixed window** (last six months including the current month) for simplicity.

---

## 14. Tradeoffs / Simplifications

- **No refresh tokens** — single JWT with expiry; login again when it expires.
- **No email verification** — registration is enough for the assignment scope.
- **No password reset** — not required for the brief.
- **Swagger** was optional; the project can be tested with Postman or curl.
- **Schema updates** via `ddl-auto=update` — fine for learning; production would use migrations.
- Focus stayed on **clear layers** (controller → service → repository) and **RBAC**, not microservices or extra infrastructure.

---

## 15. Conclusion

This backend delivers a working **finance dashboard API** with **JWT**, **MySQL**, and **role-based access** that matches the assignment goals. It is meant to be **easy to run**, **easy to test**, and **easy to explain** in an interview—without unnecessary complexity.

If something fails to start, check: MySQL running, database created, credentials in `application.properties`, port **8080** free, and JWT secret length.
