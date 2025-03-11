package org.application.hotelbookingappbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class HotelBookingAppBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelBookingAppBeApplication.class, args);
        // System.out.println(new BCryptPasswordEncoder().encode("admin123"));     // Used for to get BCRYPT password when manually create ADMIN role
    }

}
