
# 📝 ToDo-Service

**ToDo-Service** is a Spring Boot microservice that provides full task management functionality for authenticated users. The service supports task creation, update, soft deletion, restoration, and listing (active and deleted) with pagination and filtering.

---
## 📦 Project Type

- Microservices Architecture
- This service is a part of the larger **ToDa App**
- Fully RESTful API

---

## 🚀 Features

- 🛡️ **JWT-based Authentication & Authorization**
- 🗂️ Create, Update, Delete, Restore Tasks
- 🔎 Retrieve all tasks with Pagination
- 🧠 Custom Validation using Spring Validator
- 🧰 Error Handling with custom exceptions and global handler
- 🔐 SecurityConfig for securing endpoints
- 📄 Swagger API documentation
- 📬 Postman collection for testing
- 🗑️ Soft delete & task restore capability
- 🧪 JUnit 5 tests for controller & service

---

## 🧰 Technologies Used

| Category           | Tech Stack                      |
|--------------------|----------------------------------|
| Language           | Java 17                         |
| Framework          | Spring Boot 3.x                 |
| Security           | Spring Security + JWT           |
| Database           | MySQL                           |
| Build Tool         | Maven                           |
| Documentation      | Swagger OpenAPI 3               |
| Testing            | JUnit 5, Mockito                |
| API Client         | Postman                         |
| Containerization   | Docker                          |

---

---

## 🔐 Authentication

All endpoints require JWT-based authentication. The authenticated user's email is extracted from the token and used to authorize actions.

---

## 🧪 Validation & Error Handling

- DTO validation using `javax.validation`
- Custom validation at service-level (e.g., title, ownership)
- Global exception handler returns a structured response:

### ❌ Validation Error Example

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": {
    "title": "Title is required"
  }
}
```
طط
## 📍 POST `/api/tasks` — Create Task

### ✅ Success

```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {
    "id": 1,
    "title": "Buy groceries",
    "priority": "HIGH"
  },
  "errors": null
}
```

### ❌ Validation Error

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": {
    "title": "Title is required"
  }
}
```

---

## 📍 GET `/api/tasks/active` — Get Active Tasks

### ✅ Success

```json
{
  "success": true,
  "message": "Tasks retrieved successfully",
  "data": {
    "content": [...],
    "pageNumber": 0,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 3
  },
  "errors": null
}
```

### ❌ Unauthorized

```json
{
  "success": false,
  "message": "Unauthorized",
  "data": null,
  "errors": null
}
```

---

## 📍 GET `/api/tasks/deleted` — Get Deleted Tasks

### ✅ Success

```json
{
  "success": true,
  "message": "Deleted Tasks retrieved successfully",
  "data": {
    "content": [...],
    "pageNumber": 0,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 1
  },
  "errors": null
}
```

### ❌ Unauthorized

```json
{
  "success": false,
  "message": "Unauthorized",
  "data": null,
  "errors": null
}
```

---

## 📍 GET `/api/tasks/{id}` — Get Task by ID

### ✅ Success

```json
{
  "success": true,
  "message": "Task retrieved successfully",
  "data": {
    "id": 1,
    "title": "Example Task",
    "details": "...",
    "createdAt": "...",
    "status": "PENDING"
  },
  "errors": null
}
```

### ❌ Not Found or Forbidden

```json
{
  "success": false,
  "message": "Task not found or access denied",
  "data": null,
  "errors": null
}
```

---

## 📍 PUT `/api/tasks/{id}` — Update Task

### ✅ Success

```json
{
  "success": true,
  "message": "Task updated successfully",
  "data": {
    "id": 1,
    "title": "Updated title",
    "priority": "LOW"
  },
  "errors": null
}
```

### ❌ Not Found or Forbidden

```json
{
  "success": false,
  "message": "Task not found or access denied",
  "data": null,
  "errors": null
}
```

---

## 📍 DELETE `/api/tasks/{id}` — Soft Delete Task

### ✅ Success

```json
{
  "success": true,
  "message": "Task deleted successfully",
  "data": null,
  "errors": null
}
```

### ❌ Not Found or Forbidden

```json
{
  "success": false,
  "message": "Task not found or access denied",
  "data": null,
  "errors": null
}
```

---

## 📍 PUT `/api/tasks/restore/{id}` — Restore Deleted Task

### ✅ Success

```json
{
  "success": true,
  "message": "Task restored successfully",
  "data": null,
  "errors": null
}
```

### ❌ Not Found or Forbidden

```json
{
  "success": false,
  "message": "Task not found or access denied",
  "data": null,
  "errors": null
}
```

---
