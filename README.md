# Service Booking & Consulting Platform

## GitHub Repository

https://github.com/viga-alt/eecs3311-service-booking

## Project Overview

A platform that connects clients with professional consultants for booking consulting sessions. EECS3311 | Software Design

## Architecture

### Backend (`backend/`)

Spring Boot REST API wrapping the Phase 1 business logic. All existing domain classes, services, and design patterns are preserved.

- `domain/` — Core entities: User, Client, Consultant, Admin, Booking, Service, TimeSlot, Payment, PaymentMethod
- `domain/state/` — State pattern: 7 concrete booking states (Requested, Confirmed, PendingPayment, Paid, Rejected, Cancelled, Completed)
- `observer/` — Observer pattern: ClientNotificationObserver, ConsultantNotificationObserver, AdminNotificationObserver, NotificationService
- `strategy/payment/` — Payment strategies: CreditCard, DebitCard, PayPal, BankTransfer
- `strategy/cancellation/` — Cancellation policies: Flexible, Strict, NoCancellation
- `singleton/` — DatabaseManager (in-memory data store)
- `policy/` — SystemPolicy (admin-configurable platform settings)
- `service/` — Business logic: BookingService, ConsultantService, PaymentService, AdminService, PaymentProcessor
- `controller/` — REST API endpoints: Auth, Service, Booking, Consultant, Payment, Admin, Chat
- `ai/` — GeminiChatService for AI customer assistant
- `config/` — Spring configuration: CORS, service beans, data initialization

### Frontend (`frontend/`)

React single-page application built with Vite. Provides role-based dashboards for Client, Consultant, and Admin users.

- `src/pages/` — LoginPage, ClientDashboard, ConsultantDashboard, AdminDashboard
- `src/components/` — Navbar, ChatBot (AI assistant widget)
- `src/api.js` — API client for all backend endpoints

### Legacy CLI (`frontend/src/main/java/`)

The Phase 1 console-based UI is preserved for reference.

## Design Patterns

1. **Singleton** — `DatabaseManager`: Single shared in-memory data store
2. **State** — `Booking` + `BookingState` hierarchy: Lifecycle transitions delegated to concrete states
3. **Observer** — `Booking` notifies registered observers on state changes
4. **Strategy (Payment)** — `PaymentProcessor` selects the right `PaymentStrategy` by method type
5. **Strategy (Cancellation)** — `BookingService` enforces the active `CancellationPolicy` at runtime

## How to Run

### Option 1: Docker (Recommended)

Requires Docker and Docker Compose.

```bash
# 1. Clone the repository
git clone https://github.com/viga-alt/eecs3311-service-booking.git
cd eecs3311-service-booking

# 2. Configure the AI chatbot (optional)
cp .env.example .env
# Edit .env and add your Gemini API key

# 3. Start all services
docker-compose up
```

This starts three containers:
- **Frontend** — http://localhost:3000
- **Backend** — http://localhost:8080
- **Database** — MySQL on port 3306

### Option 2: Local Development

**Backend** (requires Java 17+ and Maven):

```bash
cd backend
mvn spring-boot:run
```

The API starts at http://localhost:8080.

**Frontend** (requires Node.js 18+):

```bash
cd frontend
npm install
npm run dev
```

The dev server starts at http://localhost:5173.

### Option 3: Legacy CLI (Phase 1)

```bash
find backend/src frontend/src -name "*.java" > sources.txt
mkdir -p out
javac -d out @sources.txt
java -cp out com.app.ui.ConsoleApp
```

## AI Customer Assistant

The platform includes an AI chatbot accessible to clients via the floating chat button. It uses Google Gemini to answer questions about services, booking process, payment methods, and cancellation policies.

Setup:
1. Get a free API key at https://aistudio.google.com/app/apikey
2. Set `GEMINI_API_KEY` in your `.env` file or environment

See [AI_CHATBOT_DOCUMENTATION.md](AI_CHATBOT_DOCUMENTATION.md) for full details.

## Demo Accounts

| Email | Password | Role |
|-------|----------|------|
| bob@email.com | pass123 | Client |
| carol@email.com | pass456 | Client |
| dave@consult.com | pass789 | Consultant (Approved) |
| eve@consult.com | passabc | Consultant (Pending) |
| admin@platform.com | admin123 | Admin |

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/login | Login with email/password |
| GET | /api/services | Browse all services |
| GET | /api/consultants/approved | List approved consultants |
| GET | /api/consultants/{id}/slots | Get consultant's available slots |
| POST | /api/consultants/{id}/slots | Add a time slot |
| GET | /api/consultants/{id}/services | Get consultant's services |
| POST | /api/bookings | Create a booking |
| GET | /api/bookings/client/{id} | Client booking history |
| DELETE | /api/bookings/{id}/client/{clientId} | Cancel a booking |
| GET | /api/bookings/consultant/{id} | Consultant's bookings |
| GET | /api/bookings/consultant/{id}/pending | Pending booking requests |
| POST | /api/bookings/{id}/accept | Accept a booking |
| POST | /api/bookings/{id}/reject | Reject a booking |
| POST | /api/bookings/{id}/complete | Complete a booking |
| POST | /api/payments | Process a payment |
| GET | /api/payments/client/{id} | Payment history |
| GET | /api/payments/methods/client/{id} | List payment methods |
| POST | /api/payments/methods/client/{id} | Add payment method |
| DELETE | /api/payments/methods/{id}/client/{clientId} | Remove payment method |
| PUT | /api/payments/methods/{id}/client/{clientId}/default | Set default method |
| GET | /api/admin/consultants/pending | Pending consultant registrations |
| POST | /api/admin/consultants/{id}/approve | Approve consultant |
| POST | /api/admin/consultants/{id}/reject | Reject consultant |
| GET | /api/admin/policy | View system policies |
| PUT | /api/admin/policy/cancellation | Set cancellation policy |
| PUT | /api/admin/policy/payment-method | Set default payment method |
| PUT | /api/admin/policy/notification | Set notification channel |
| GET | /api/admin/status | System status overview |
| POST | /api/chat | AI chatbot |

## Docker Configuration

| Service | Port | Description |
|---------|------|-------------|
| frontend | 3000 | Nginx serving React build, proxies /api to backend |
| backend | 8080 | Spring Boot REST API |
| db | 3306 | MySQL 8.0 with persistent volume |
