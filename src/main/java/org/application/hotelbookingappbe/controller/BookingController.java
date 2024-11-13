package org.application.hotelbookingappbe.controller;

import org.application.hotelbookingappbe.dto.BookingDto;
import org.application.hotelbookingappbe.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/bookings")
@RestController
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return new ResponseEntity<>(bookingService.getAllBookings(), HttpStatus.OK);
    }

    @GetMapping("/confirmation-code/{confirmationCode}")
    public ResponseEntity<BookingDto> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        return new ResponseEntity<>(bookingService.getBookingByConfirmationCode(confirmationCode), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<BookingDto>> getBookingsByEmail(@PathVariable String email) {
        return new ResponseEntity<>(bookingService.getBookingsByEmail(email), HttpStatus.OK);
    }

    @PostMapping("/add-booking/{roomId}")
    public ResponseEntity<BookingDto> addBooking(@PathVariable Long roomId, @RequestBody BookingDto bookingDto) {
        return new ResponseEntity<>(bookingService.addBooking(roomId, bookingDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-booking/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long bookingId) {
        bookingService.deleteBooking(bookingId);
        return new ResponseEntity<>("Booking deleted successfully", HttpStatus.OK);
    }
}
