# AI Customer Assistant — Documentation

## Overview

The AI Customer Assistant is a chatbot integrated into the client interface that helps users understand the platform, discover services, and navigate the booking process. It is powered by Google Gemini (free tier) and accessed through a backend service — it never directly queries the database or accesses private user data.

## Architecture

```
Client Browser  →  React ChatBot Component  →  POST /api/chat  →  ChatController  →  GeminiChatService  →  Gemini API
```

1. The client opens the chatbot by clicking the floating button in the bottom-right corner.
2. The client types a question and submits it.
3. The React frontend sends a POST request to `/api/chat` with `{ "message": "..." }`.
4. The `ChatController` passes the message to `GeminiChatService`.
5. `GeminiChatService` constructs a prompt that includes:
   - A system instruction defining the assistant's role and boundaries.
   - General platform information (services, booking process, payment methods, policies).
   - The user's question.
6. The service calls the Gemini REST API and returns the response.
7. The response is sent back to the frontend and displayed in the chat window.

## Functionality

The chatbot can answer questions about:

- **Available services**: Names, durations, prices, and descriptions of consulting services listed on the platform.
- **Booking process**: Step-by-step explanation of how to browse, select, book, pay, and complete a session.
- **Payment methods**: Supported types (Credit Card, Debit Card, PayPal, Bank Transfer) and their validation requirements.
- **Cancellation policies**: Flexible (full refund), Strict (25% refund), and No Cancellation.
- **Platform features**: General guidance on using the platform as a client.

## Example Questions and Responses

| Question | Expected Response |
|----------|------------------|
| "How do I book a consulting session?" | Explains the step-by-step booking workflow |
| "What payment methods do you accept?" | Lists Credit Card, Debit Card, PayPal, Bank Transfer |
| "Can I cancel my booking?" | Explains the three cancellation policies |
| "What types of consulting services are available?" | Lists the current services with prices and durations |
| "How does payment work?" | Explains the payment flow after booking confirmation |

## System Context Provided to the AI

The following public information is provided in the system prompt:

- Platform description and purpose
- List of available services (name, duration, price, description)
- Booking lifecycle steps (Requested → Confirmed → Pending Payment → Paid → Completed)
- Supported payment methods and validation rules
- Cancellation policy descriptions
- Usage guidelines and behavioral boundaries

## Privacy and Safety Measures

- **No database access**: The AI service reads only from publicly available service listings. It does not query the database for user data.
- **No personal data**: The system prompt explicitly instructs the AI to never share personal information, payment details, or private booking data.
- **No automated actions**: The AI provides information only — it cannot create bookings, process payments, or modify any system state.
- **Input sanitization**: User messages are escaped before being included in the API request.
- **Error handling**: API failures are caught gracefully with user-friendly error messages.

## API Integration

- **Provider**: Google Gemini (Generative AI)
- **Model**: `gemini-2.5-flash` (stable ID from [Google’s model list](https://ai.google.dev/gemini-api/docs/models); do not use invalid preview strings like `gemini-2.5-flash-preview-04-17`)
- **Endpoint**: `https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent`
- **Authentication**: API key passed as query parameter
- **Configuration**: API key set via `GEMINI_API_KEY` environment variable

## Setup

1. Obtain a free Gemini API key at https://aistudio.google.com/app/apikey
2. Set the environment variable: `GEMINI_API_KEY=your_key_here`
3. When using Docker, add the key to a `.env` file (see `.env.example`)
4. The chatbot will be accessible from the client dashboard via the floating chat button
