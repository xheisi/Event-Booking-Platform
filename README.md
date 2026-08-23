# EventBooking

A backend platform for organizers to publish events and attendees to browse,
book, and review them. Built with Spring Boot, Spring Security (JWT),
Spring Data JPA, and MySQL.

## Roles

- **ADMIN** — manages venues, categories, and user accounts; can view and
  cancel any booking; can view all events.
- **ORGANIZER** — creates, publishes, updates, and cancels their own events;
  views bookings made against their events.
- **ATTENDEE** — books and cancels their own bookings, joins waitlists,
  leaves reviews.
- **Unauthenticated** — can register, log in, and browse/search published
  events.

## Tech stack

- Java 21, Spring Boot 4.1.0
- Spring Web (MVC), Spring Data JPA, Spring Security, JWT (jjwt)
- MySQL (dev), H2 in-memory (test/`int` profile)
- Log4j2 for logging
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito

## Prerequisites

- Java 21 (JDK)
- Maven (or use the included `mvnw`/`mvnw.cmd` wrapper)
- MySQL running locally
- An IDE (IntelliJ recommended) or a terminal

## 1. Clone and open the project

```bash
git clone <repo-url>
cd eventbooking
```

## 2. Create the database

The `dev` profile expects a MySQL database named `eventbooking_dev`. Create
it once, e.g. in DBeaver or via the MySQL CLI:

```sql
CREATE DATABASE eventbooking_dev;
```

If you'd rather use a different name, create that database instead and
update `spring.datasource.url` in `src/main/resources/application-dev.properties`
to match (just change the database name at the end of the URL, everything
else stays the same).

The MySQL username/password in `application-dev.properties` default to
`root` with no password — update those two lines if your local MySQL setup
uses different credentials.

Tables are created automatically on first run (`spring.jpa.hibernate.ddl-auto=update`)
— no manual schema setup needed.

## 3. Configure the JWT secret and admin seed password

Both currently live directly in `src/main/resources/application-dev.properties`:

```properties
jwt.secret=<a Base64-encoded random string>
jwt.expiration-ms=86400000
admin.seed.password=<a password of your choice>
```

If you want to generate your own secret rather than reusing the one already
in the file:

Generate one online or:

**PowerShell:**
```powershell
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

**Mac/Linux:**
```bash
openssl rand -base64 32
```

Paste the output as the `jwt.secret` value. It must be valid Base64 — if you
type a plain sentence instead, the app will fail to start with a Base64
decoding error.

`admin.seed.password` can be any non-blank string — it's the login password
for the one admin account the app creates automatically the first time it
runs (username `admin`, configurable via `admin.seed.username`). If left
blank, seeding is skipped and no error is thrown.

> These values are currently plain text in a config file for local
> development convenience. For anything beyond local testing, they should
> be moved to environment variables instead (`jwt.secret=${JWT_SECRET}`,
> etc.) and never committed with real values.

## 4. Run the application

```bash
./mvnw spring-boot:run
```
(or `mvnw.cmd spring-boot:run` on Windows, or run `EventbookingApplication`
directly from your IDE)

The app starts on `http://localhost:8080`, using the `dev` profile by
default (set in `application.properties`).

On first run, it automatically creates the tables and seeds one admin
account (`admin` / whatever you set as `admin.seed.password`).

## 5. Log in as admin and get started

```
POST http://localhost:8080/api/auth/login
{ "username": "admin", "password": "<your admin.seed.password>" }
```

Use the returned token (as a Bearer token) to create venues and categories,
which are needed before any event can be created. Organizers and attendees
can self-register via `POST /api/auth/register-organizer` and
`POST /api/auth/register`.

## API documentation

Once running, interactive API docs are available at:

```
http://localhost:8080/swagger-ui/index.html
```

Every endpoint, its expected request/response shape, and auth requirements
are listed there. Click the padlock icon to paste a Bearer token and try
authenticated endpoints directly from the browser.

## Configuration profiles

- **`dev`** (default) — local MySQL, `ddl-auto=update`, used for everyday
  development and manual testing.
- **`int`** — used automatically by the test suite. Points at an in-memory
  H2 database (`ddl-auto=create-drop`, fresh schema every run). Never run
  manually.
- **`prod`** — reads all credentials from environment variables
  (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`,
  `ADMIN_SEED_PASSWORD`), `ddl-auto=validate`. Included to demonstrate the
  pattern for a real deployment; not runnable without an actual production
  database to point it at.

## Running the tests

```bash
./mvnw test
```

Runs the full suite: unit tests (JUnit + Mockito) for every service, plus
integration tests (`@SpringBootTest`, `int` profile) covering booking
creation, seat validation, waitlist promotion, and event-cancellation
cascading through real HTTP requests against a real (in-memory) database.

## Logs

Application logs (Log4j2) are written to `logs/eventbooking.log` once the
app has run at least once. This folder is gitignored — it's regenerated
locally, not part of the repository.
