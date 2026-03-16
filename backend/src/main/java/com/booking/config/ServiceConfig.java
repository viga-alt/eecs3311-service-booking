package com.booking.config;

import com.booking.policy.SystemPolicy;
import com.booking.service.AdminService;
import com.booking.service.BookingService;
import com.booking.service.ConsultantService;
import com.booking.service.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public SystemPolicy systemPolicy() {
        return new SystemPolicy();
    }

    @Bean
    public ConsultantService consultantService() {
        return new ConsultantService();
    }

    @Bean
    public BookingService bookingService(SystemPolicy systemPolicy) {
        return new BookingService(systemPolicy.getCancellationPolicy());
    }

    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }

    @Bean
    public AdminService adminService(ConsultantService consultantService, SystemPolicy systemPolicy) {
        return new AdminService(consultantService, systemPolicy);
    }
}
