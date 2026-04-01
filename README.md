# Secure E-Commerce Application

A full-stack e-commerce platform built with Spring Boot and React. Features a complete shopping experience with secure JWT authentication, role-based access control, rate limiting, and a modern TypeScript frontend.

> The React frontend was developed with the assistance of [Claude](https://claude.ai) (Anthropic's AI).

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2, Spring Security |
| Database | MySQL 8.x (JPA/Hibernate) |
| Auth | JWT (access + refresh tokens), BCrypt |
| Caching | Redis |
| Rate Limiting | Bucket4j |
| Email | Resend |
| API Docs | Swagger / OpenAPI 3.0 |
| Frontend | React 18, TypeScript, Vite, Tailwind CSS |
| State | TanStack Query, React Context |
| Forms | React Hook Form + Zod |

---

## Features

**Shop**
- Product listing with search, category filter, price range, and pagination
- Product detail with image, ratings, and reviews
- Shopping cart with quantity controls
- Checkout with address selection, payment method, and coupon validation
- Order history and shipment tracking
- Wishlist

**Auth**
- Register, login, logout (single device or all devices)
- Forgot/reset password via email
- JWT access tokens (15 min) + refresh tokens (7 days) with rotation
- Up to 5 concurrent sessions per user

**Admin Panel**
- Product management (create, edit, delete, toggle active)
- Category management (hierarchical)
- Order management (update status)
- Shipment management (carrier, tracking number)
- Coupon management (percentage or fixed discount, expiry, usage limits)

**Security**
- Rate limiting on auth and order endpoints
- Role-based access control (USER / ADMIN)
- Password reset tokens (one-time, time-limited)
- Stateful refresh token storage with IP tracking

---

## Project Structure

```
SecureEcommerceApplication/
├── src/main/java/.../
│   ├── controller/       # REST controllers (11)
│   ├── service/          # Business logic
│   ├── repository/       # JPA repositories
│   ├── entity/           # Database entities
│   ├── dto/              # Request / Response DTOs
│   ├── security/         # JWT, filters, rate limiter
│   ├── mapper/           # MapStruct mappers
│   ├── exception/        # Global exception handler
│   └── scheduler/        # Scheduled tasks (token cleanup)
├── src/main/resources/
│   └── application.properties
├── frontend/             # React frontend (built with Claude)
│   ├── src/
│   │   ├── pages/        # Page components
│   │   ├── components/   # Reusable UI components
│   │   ├── api/          # Axios API calls
│   │   ├── context/      # Auth + Cart context
│   │   └── types/        # TypeScript types
│   └── vite.config.ts
└── pom.xml
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.x
- Node.js 18+ and npm
- Redis (optional, for caching)

### 1. Database

```sql
CREATE DATABASE ecommerce_db;
```

### 2. Backend Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD

# JWT — use a strong random 256-bit key in production
application.security.jwt.secret-key=YOUR_SECRET_KEY
app.jwt.access-token-expiration-seconds=900
app.jwt.refresh-token-expiration-seconds=604800
app.jwt.max-active-sessions=5

# Email (Resend)
resend.api-key=YOUR_RESEND_API_KEY
resend.from=noreply@yourdomain.com
app.reset-password.url=http://localhost:3000/reset-password
```

### 3. Run the Backend

```bash
mvn clean spring-boot:run
```

Backend starts on `http://localhost:8080`.
API docs available at `http://localhost:8080/swagger-ui.html`.

### 4. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend starts on `http://localhost:3000`. Vite proxies all `/api` requests to the backend automatically.

### 5. Build for Production

```bash
# Backend
mvn clean package
java -jar target/secure-ecommerce-api-0.0.1-SNAPSHOT.jar

# Frontend
cd frontend
npm run build
```

---

## API Overview

### Public

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register |
| POST | `/api/v1/auth/login` | Login |
| POST | `/api/v1/auth/refresh` | Refresh access token |
| POST | `/api/v1/auth/logout` | Logout |
| POST | `/api/v1/auth/forgot-password` | Request password reset |
| POST | `/api/v1/auth/reset-password` | Reset password |
| GET | `/api/v1/products` | List products (paginated) |
| GET | `/api/v1/products/{id}` | Product detail |
| GET | `/api/v1/products/search` | Search products |
| GET | `/api/v1/categories` | List categories |

### Authenticated Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET/PUT | `/api/v1/users/me` | Profile |
| GET/POST/PUT/DELETE | `/api/v1/users/me/addresses` | Addresses |
| GET/POST/PUT/DELETE | `/api/v1/cart/items` | Cart |
| POST/GET | `/api/v1/orders` | Place and view orders |
| GET/POST/DELETE | `/api/v1/wishlist` | Wishlist |
| POST/DELETE | `/api/v1/products/{id}/reviews` | Reviews |

### Admin Only

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST/PUT/DELETE | `/api/v1/products` | Manage products |
| POST/PUT/DELETE | `/api/v1/categories` | Manage categories |
| GET/PATCH | `/api/v1/admin/orders` | View and update orders |
| PUT | `/api/v1/admin/orders/{id}/shipment` | Update shipment |
| GET/POST/PUT/DELETE | `/api/v1/admin/coupons` | Manage coupons |

Full interactive docs: `http://localhost:8080/swagger-ui.html`

---

## Environment Variables

Sensitive values should be kept out of source control. Set them as environment variables or in a `.env` file (excluded from git):

```
DB_URL=jdbc:mysql://localhost:3306/ecommerce_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
JWT_SECRET=your_256bit_secret
RESEND_API_KEY=your_resend_key
```

---

## License

MIT
