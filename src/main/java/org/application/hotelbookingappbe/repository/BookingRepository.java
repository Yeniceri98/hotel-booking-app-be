package org.application.hotelbookingappbe.repository;

import org.application.hotelbookingappbe.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingConfirmationCode(String bookingConfirmationCode);
    List<Booking> findByGuestEmail(String email);
}
