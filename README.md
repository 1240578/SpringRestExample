# TodoApp — REST API

A simple Todo List REST API built with Spring Boot and Spring Data JPA. It demonstrates a clean layering (Controller → Service → Repository), DTO-based I/O, proper HTTP semantics, and JPA entity relationships.

---

## Overview
- Manage todo lists and their items via REST endpoints under `/api/v1/lists`.
- Uses an embedded H2 database by default (see `src/main/resources/application.properties`).
- Follows common Spring Boot best practices for web and persistence.

### Tech stack
- Java 17+
- Spring Boot (Web, Data JPA)
- H2 Database (dev)
- Jakarta Bean Validation (via `spring-boot-starter-validation`)
- Lombok (to reduce boilerplate)

---

## Project structure
```
src/main/java/com/toufarto/todoApp
├─ TodoAppApplication.java           # Spring Boot entry point
├─ controllers/
│  └─ TodoListController.java        # REST controller (thin HTTP adapter)
├─ service/
│  └─ TodoService.java               # Business logic + transactions
├─ repository/
│  └─ TodoListRepository.java        # Spring Data repository for TodoList
├─ domain/
│  ├─ TodoList.java                  # JPA entity (List) — one-to-many items
│  └─ TodoItem.java                  # JPA entity (Item) — many-to-one list
└─ domain/dto/
   ├─ CreateTodoListRequest.java     # Request DTO with validation
   ├─ CreateTodoItemRequest.java     # Request DTO with validation
   ├─ TodoListDto.java               # Response DTO
   └─ TodoItemDto.java               # Response DTO
```

---

## Domain model
- `TodoList`
  - Fields: `id`, `name`, `items`
  - Mapping: `@OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)`
  - Methods: `addItem(item)`, `removeItem(item)` maintain both sides of the relationship
- `TodoItem`
  - Fields: `id`, `description`, `list`
  - Mapping: `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "list_id")`

Note: In the current codebase, IDs are defined as primitive `long`. Consider switching to `Long` (boxed) to better represent "not yet persisted" as `null` and avoid confusion with default `0`.

---

## API endpoints
Base path: `/api/v1/lists`

- `GET /api/v1/lists`
  - Returns all todo lists as an array of `TodoListDto`.
- `POST /api/v1/lists`
  - Body: `CreateTodoListRequest { name }`
  - Response: `201 Created`, `Location: /api/v1/lists/{id}`, body `TodoListDto`.
- `GET /api/v1/lists/{id}`
  - Returns one list (404 if not found).
- `GET /api/v1/lists/{id}/todos`
  - Returns the list’s items as `TodoItemDto[]`.
- `POST /api/v1/lists/{listId}/todos`
  - Body: `CreateTodoItemRequest { description }`
  - Response: `201 Created`, `Location: /api/v1/lists/{listId}/todos/{todoId}`, body `TodoItemDto`.
- `DELETE /api/v1/lists/{listId}/todos/{todoId}`
  - Deletes one item from the list. Response: `204 No Content`.

HTTP semantics used:
- `201 Created` with `Location` on successful POST (resource creation).
- `200 OK` for reads; `404 Not Found` when a resource doesn’t exist.
- `204 No Content` for delete.

---

## Best practices applied

### 1) Thin Controller, Fat Service (clear layering)
- `TodoListController` only handles routing, request/response mapping, and HTTP status codes.
- `TodoService` encapsulates business operations (create list, add/remove item) and owns transaction boundaries.

### 2) DTOs for requests and responses
- Request DTOs (`CreateTodoListRequest`, `CreateTodoItemRequest`) are annotated with Bean Validation.
- Response DTOs (`TodoListDto`, `TodoItemDto`) prevent exposing JPA internals and avoid lazy-loading/recursion pitfalls during JSON serialization.

### 3) Validation at the edge
- `@Valid @RequestBody` in controller methods ensures inputs are checked (e.g., `@NotBlank`, `@Size`).
- Invalid inputs result in `400 Bad Request` automatically (can be customized via `@ControllerAdvice`).

### 4) Proper HTTP semantics
- `POST` returns `201 Created` with `Location` header pointing to the newly created resource.
- `DELETE` returns `204 No Content`.
- Missing resources produce `404 Not Found` (via `ResponseStatusException`).

### 5) Correct JPA mappings and cascading
- Bi-directional relationship maintained via helper methods (`addItem`, `removeItem`).
- `cascade = ALL` + `orphanRemoval = true` keeps child lifecycle consistent with parent.

### 6) Transactional service methods
- Write operations are marked transactional to ensure changes are persisted as a unit of work.
- Reads can be optimized with `@Transactional(readOnly = true)`.

### 7) URI building via `UriComponentsBuilder`
- Avoids string concatenation, ensures consistent path construction and proper variable expansion.

### 8) Repository abstraction (Spring Data JPA)
- `TodoListRepository` provides persistence operations, enabling concise code and testability.

---

## Open items and suggested improvements
- ID types: switch `long` → `Long` in entities for better pre-persist semantics.
- Transaction annotation: prefer Spring’s `org.springframework.transaction.annotation.Transactional` over `jakarta.transaction.Transactional` for consistent Spring-managed transactions.
- Immediate ID availability on create item: call `saveAndFlush` (or `flush`) before returning, so the generated ID is present in responses.
- Global error handling: add a `@ControllerAdvice` to standardize error responses (e.g., validation errors, 404s).
- Pagination: add `Pageable` to collection endpoints once data grows.
- OpenAPI: add `springdoc-openapi` for interactive API docs at `/swagger-ui.html`.
- Security: integrate Spring Security (even basic) if exposed beyond local use.
- Tests: add slice tests (`@WebMvcTest`) for the controller and unit tests for the service.

---

## Run locally
Using Maven Wrapper (recommended):
```
./mvnw spring-boot:run        # PowerShell: .\mvnw.cmd spring-boot:run
```
The app starts on `http://localhost:8080`.

---

## Quick test (Windows, curl.exe)
Create a list:
```
curl.exe -X POST "http://localhost:8080/api/v1/lists" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Groceries\"}"
```
Add an item (replace `{id}`):
```
curl.exe -X POST "http://localhost:8080/api/v1/lists/{id}/todos" ^
  -H "Content-Type: application/json" ^
  -d "{\"description\":\"Buy milk\"}"
```
List items:
```
curl.exe -X GET "http://localhost:8080/api/v1/lists/{id}/todos"
```
Delete an item (replace `{listId}`, `{todoId}`):
```
curl.exe -X DELETE "http://localhost:8080/api/v1/lists/{listId}/todos/{todoId}"
```

Expected status codes:
- Create: `201` + `Location` header
- Read: `200` (or `404` if missing)
- Delete: `204`
- Validation failure: `400`

---

## Troubleshooting
- `400 Bad Request` on POST: ensure `Content-Type: application/json` and valid JSON body.
- `404 Not Found`: verify IDs and that resources exist.
- Item ID is `0` in response: switch entity IDs to `Long` and call `saveAndFlush`/`flush` in the service before returning, or persist the item directly via a dedicated repository.

---
