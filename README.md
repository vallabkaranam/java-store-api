# Java Store API

A production-deployed Spring Boot REST API for a grocery store backend. The API models a realistic ecommerce flow: browse products, create a cart, authenticate users, check out with Stripe, receive payment webhooks, and view order history.

This project is intentionally built around backend patterns used in industry: stateless JWT authentication, role-based authorization, feature-oriented package structure, database migrations, external payment integration, environment-based configuration, DTO mapping, transactional service-layer workflows, and production error monitoring.

## Live API

| Resource | Link |
| --- | --- |
| Swagger UI | [java-store-api-production.up.railway.app/swagger-ui/index.html](https://java-store-api-production.up.railway.app/swagger-ui/index.html) |
| Base URL | [java-store-api-production.up.railway.app](https://java-store-api-production.up.railway.app) |

## What This API Supports

| Domain | Capabilities |
| --- | --- |
| Products | Public product browsing, admin-only product creation/update/delete |
| Carts | Anonymous cart creation, add/update/remove cart items, clear cart |
| Auth | User registration, login, refresh token flow, current-user lookup |
| Orders | Authenticated order history and single-order lookup |
| Checkout | Cart-to-order conversion, Stripe Checkout session creation |
| Payments | Stripe webhook handling for paid/failed payment status updates |
| Admin | Role-protected admin endpoint for authorization verification |

## Engineering Highlights

- **Spring Boot REST API** with feature-based modular organization that keeps controllers, services, repositories, DTOs, mappers, exceptions, and security rules close to their domain.
- **Spring Security** with stateless JWT auth, BCrypt password hashing, custom JWT filter, refresh-token cookie flow, and role-based access control.
- **Modular authorization rules** using per-feature `SecurityRules` components, so each feature owns its route permissions.
- **Stripe integration** behind a `PaymentGateway` interface, keeping checkout logic decoupled from the Stripe SDK.
- **Webhook-driven order status updates** for Stripe payment success/failure events, including Stripe signature verification.
- **Transactional checkout flow** that creates an order, starts payment, clears the cart, and rolls back/deletes the order on payment-session failure.
- **Flyway migrations** for repeatable schema evolution and database seeding.
- **MySQL + Spring Data JPA** for persistence, relationships, repositories, and domain entities.
- **DTO boundaries with MapStruct** to avoid leaking persistence entities directly through API responses.
- **Environment-specific configuration** with Spring profiles and externalized variables for local development and production deployment.
- **Production observability** with Sentry error monitoring, environment tagging, and verified exception capture from the deployed Railway service.
- **OpenAPI/Swagger documentation** generated through `springdoc-openapi`.

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Java 17 target |
| Framework | Spring Boot 3.4 |
| API | Spring MVC, Bean Validation, OpenAPI/Swagger |
| Security | Spring Security, JWT, BCrypt, RBAC |
| Persistence | Spring Data JPA, Hibernate, MySQL |
| Migrations | Flyway |
| Payments | Stripe Checkout, Stripe Webhooks |
| Mapping | MapStruct |
| Observability | Sentry |
| Deployment | Railway |
| Build | Maven |

## API Overview

Public endpoints:

```http
GET  /products
GET  /products/{id}
POST /users
POST /auth/login
POST /auth/refresh
POST /carts
GET  /carts/{cartId}
POST /carts/{cartId}/items
PUT  /carts/{cartId}/items/{productId}
DELETE /carts/{cartId}/items/{productId}
DELETE /carts/{cartId}/items
POST /checkout/webhook
```

Authenticated endpoints:

```http
GET  /auth/me
POST /checkout
GET  /orders
GET  /orders/{orderId}
```

Admin-only endpoints:

```http
POST   /products
PUT    /products/{id}
DELETE /products/{id}
GET    /admin/hello
```

## End-to-End User Flow

The API supports a common ecommerce flow: customers can browse products and build a cart before creating an account. Authentication is required when checking out and viewing orders.

1. Browse the grocery catalog without logging in:

```http
GET /products
GET /products/{id}
```

2. Create a guest cart:

```http
POST /carts
```

3. Add products to the cart:

```http
POST /carts/{cartId}/items
Content-Type: application/json

{
  "productId": 1
}
```

Cart items can also be updated or removed:

```http
PUT    /carts/{cartId}/items/{productId}
DELETE /carts/{cartId}/items/{productId}
```

4. Register and log in before checkout:

```http
POST /users
POST /auth/login
```

5. Check out with the authenticated user:

```http
POST /checkout
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "cartId": "550e8400-e29b-41d4-a716-446655440000"
}
```

The response includes the order ID and Stripe Checkout URL:

```json
{
  "orderId": 1,
  "checkoutUrl": "https://checkout.stripe.com/..."
}
```

6. Open the returned Stripe Checkout URL and complete payment.

7. Stripe calls the webhook to update the order status:

```http
POST /checkout/webhook
```

This endpoint is public because Stripe calls it directly, but webhook signatures are verified before payment events are trusted.

8. View order history or check a specific order status:

```http
GET /orders
GET /orders/{orderId}
```

Orders include a payment status such as `PENDING`, `PAID`, `FAILED`, or `CANCELED`.

## Database

Flyway manages schema changes and seed data:

| Migration | Purpose |
| --- | --- |
| `V1__initial_migration.sql` | Users, profiles, addresses, categories, products, wishlist |
| `V2__create_cart_tables.sql` | Carts and cart items |
| `V3__add_role_to_users.sql` | User roles |
| `V4__add_order_tables.sql` | Orders and order items |
| `V5__populate_database.sql` | Grocery categories and sample products |

## Running Locally

Prerequisites:

- Java 17+
- Maven
- MySQL
- Stripe CLI, if testing webhooks locally

Create a local environment file:

```bash
cp .env.example .env
```

Set:

```env
JWT_SECRET=
STRIPE_SECRET_KEY=
STRIPE_WEBHOOK_SECRET_KEY=
SENTRY_DSN=
SENTRY_ENVIRONMENT=dev
SENTRY_TRACES_SAMPLE_RATE=0.0
```

Start MySQL, then run:

```bash
./mvnw spring-boot:run
```

Local Swagger UI:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Production Configuration

The app uses Spring profiles:

- `dev` for local MySQL and local frontend URL
- `prod` for Railway-managed environment variables

Required production variables:

```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
STRIPE_SECRET_KEY=
STRIPE_WEBHOOK_SECRET_KEY=
SENTRY_DSN=
SENTRY_ENVIRONMENT=production
SENTRY_TRACES_SAMPLE_RATE=0.0
```

For Railway MySQL, the datasource URL should be a JDBC URL, for example:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
```

## Project Structure

```text
src/main/java/com/vallab/store
├── admin
├── auth
├── carts
├── common
├── orders
├── payments
├── products
└── users
```

Each feature package owns its controllers, DTOs, entities, repositories, services, mappers, exceptions, and security rules where applicable. This keeps related code close together and makes the codebase easier to extend feature by feature.
