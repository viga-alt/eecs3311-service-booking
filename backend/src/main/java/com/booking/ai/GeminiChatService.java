package com.booking.ai;

import com.booking.domain.Service;
import com.booking.singleton.DatabaseManager;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeminiChatService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String model;

    public String chat(String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            return "AI assistant is not configured. Please set the GEMINI_API_KEY environment variable.";
        }

        try {
            String systemContext = buildSystemContext();
            String fullPrompt = systemContext + "\n\nUser question: " + userMessage;

            Client client = Client.builder().apiKey(apiKey).build();
            GenerateContentResponse response = client.models.generateContent(model, fullPrompt, null);

            String text = response.text();
            return (text != null && !text.isBlank()) ? text : "I couldn't generate a response. Please try again.";
        } catch (Exception e) {
            System.out.println("[GeminiChatService] Error: " + e.getMessage());
            return "Sorry, I encountered an error processing your request. Please try again later.";
        }
    }

    private String buildSystemContext() {
        DatabaseManager db = DatabaseManager.getInstance();
        List<Service> services = db.findAllServices();

        StringBuilder ctx = new StringBuilder();
        ctx.append("You are a helpful customer assistant for the Service Booking & Consulting Platform. ");
        ctx.append("You help clients understand the platform, available services, booking process, payment methods, and policies.\n\n");
        ctx.append("PLATFORM INFORMATION:\n");
        ctx.append("- This platform connects clients with professional consultants for consulting services.\n");
        ctx.append("- Services include: software consulting, career advising, technical support, code review.\n");
        ctx.append("- Clients can browse services, book consulting sessions, and pay online.\n\n");

        ctx.append("AVAILABLE SERVICES:\n");
        for (Service s : services) {
            ctx.append(String.format("- %s: %d minutes, $%.2f — %s\n",
                    s.getName(), s.getDurationMinutes(), s.getBasePrice(), s.getDescription()));
        }

        ctx.append("\nBOOKING PROCESS:\n");
        ctx.append("1. Browse available consulting services\n");
        ctx.append("2. Select a consultant and an available time slot\n");
        ctx.append("3. Submit a booking request (status: Requested)\n");
        ctx.append("4. Consultant reviews and accepts/rejects the request\n");
        ctx.append("5. If accepted, booking moves to Pending Payment\n");
        ctx.append("6. Client processes payment using a saved payment method\n");
        ctx.append("7. After payment, session is confirmed (status: Paid)\n");
        ctx.append("8. After the session, consultant marks it as Completed\n\n");

        ctx.append("PAYMENT METHODS SUPPORTED:\n");
        ctx.append("- Credit Card (16-digit card number, expiry date, CVV)\n");
        ctx.append("- Debit Card (same validation as credit card)\n");
        ctx.append("- PayPal (valid email address)\n");
        ctx.append("- Bank Transfer (account number 8-17 digits, routing number 9 digits)\n\n");

        ctx.append("CANCELLATION POLICIES:\n");
        ctx.append("- Flexible: Full refund if cancelled in advance\n");
        ctx.append("- Strict: 25% refund only\n");
        ctx.append("- No Cancellation: Cannot cancel once confirmed\n\n");

        ctx.append("IMPORTANT RULES:\n");
        ctx.append("- Never share personal user data or private booking details\n");
        ctx.append("- Only provide general platform information and public service descriptions\n");
        ctx.append("- Be helpful, friendly, and concise in your responses\n");
        ctx.append("- If asked about specific user accounts or private data, politely decline\n");

        return ctx.toString();
    }
}
