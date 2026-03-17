# Loan Decision Engine 💵

## 🚀 Project Overview

This project is a functional prototype of a Loan Decision Engine designed for a seamless banking experience. The system evaluates loan applications based on a user's personal code, requested amount, and period, automatically optimizing the offer to provide the maximum possible credit limit.The solution consists of a Spring Boot backend and a Vanilla JavaScript frontend, designed with a focus on brand consistency and high performance.

## 🏗️ Architecture & Thought Process

The application was built with a strong emphasis on Domain-Driven Design (DDD) and Test-Driven Development (TDD).

**Core Principles:**
- Separation of Concerns: The business logic is encapsulated in the domain package, remaining entirely independent of the Spring framework and external infrastructure.
- Domain-Centric Logic: All credit score calculations and decision-making processes reside in the domain, ensuring the core engine is easily testable and maintainable.
- Infrastructure Layer: Handles external communication, including REST controllers, DTO mapping, and input validation to ensure the system only processes valid data.

## 🛠️ Tech Stack

- **Backend:** Java 21, Spring Boot 3, Lombok, Jakarta Validation.
- **Frontend:** Vanilla JavaScript (ES6+), HTML5, CSS3.
- **Testing:** JUnit 5, AssertJ, Spring RestTestClient.

## 🚦 Getting Started

### Prerequisites:
- JDK 21
- Maven 3.x

### Running backend:
```
mvn spring-boot:run
```
The API will be available at `http://localhost:8080`.

### Running frontend:
Simply open `frontend/index.html` in your browser

## ✅ Verified Personal codes in Repository:
- `49002010965` – Debt (Rejection) 
- `49002010976` – Segment 1 (Modifier: 100) 
- `49002010987` – Segment 2 (Modifier: 300) 
- `49002010998` – Segment 3 (Modifier: 1000)
