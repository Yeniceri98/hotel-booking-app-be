package org.application.hotelbookingappbe.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.application.hotelbookingappbe.dto.BookingDto;
import org.application.hotelbookingappbe.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Booking Controller", description = "Booking API")
@RequestMapping("/api/bookings")
@RestController
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Tag(name = "Get All Bookings")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return new ResponseEntity<>(bookingService.getAllBookings(), HttpStatus.OK);
    }

    @Tag(name = "Get Booking By Confirmation Code")
    @GetMapping("/confirmation-code/{confirmationCode}")
    public ResponseEntity<BookingDto> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        return new ResponseEntity<>(bookingService.getBookingByConfirmationCode(confirmationCode), HttpStatus.OK);
    }

    @Tag(name = "Get Bookings By Email")
    @GetMapping("/email/{email}")
    public ResponseEntity<List<BookingDto>> getBookingsByEmail(@PathVariable String email) {
        return new ResponseEntity<>(bookingService.getBookingsByEmail(email), HttpStatus.OK);
    }

    @Tag(name = "Add Booking")
    @PostMapping("/add-booking/{roomId}")
    public ResponseEntity<BookingDto> addBooking(@PathVariable Long roomId, @Valid @RequestBody BookingDto bookingDto) {
        return new ResponseEntity<>(bookingService.addBooking(roomId, bookingDto), HttpStatus.CREATED);
    }

    @Tag(name = "Delete Booking")
    @DeleteMapping("/delete-booking/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long bookingId) {
        bookingService.deleteBooking(bookingId);
        return new ResponseEntity<>("Booking deleted successfully", HttpStatus.OK);
    }
}
