package org.application.hotelbookingappbe.controller;

import org.application.hotelbookingappbe.dto.BookedRoomResponseDto;
import org.application.hotelbookingappbe.model.BookedRoom;
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

    @GetMapping("/all-booked-rooms")
    public ResponseEntity<List<BookedRoomResponseDto>> getAllBookedRooms() {
        return new ResponseEntity<>(bookingService.getAllBookedRooms(), HttpStatus.OK);
    }

    @GetMapping("/all-booked-rooms/{bookingConfirmationCode}")
    public ResponseEntity<BookedRoom> getBookingByConfirmationCode(@PathVariable String bookingConfirmationCode) {
        return new ResponseEntity<>(bookingService.getBookingByConfirmationCode(bookingConfirmationCode), HttpStatus.OK);
    }

    @PostMapping("/add-booking/{roomId}")
    public ResponseEntity<BookedRoomResponseDto> addBooking(@PathVariable Long roomId, @RequestBody BookedRoom bookedRoom) {
        return new ResponseEntity<>(bookingService.addBooking(roomId, bookedRoom), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-booking/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long bookingId) {
        bookingService.deleteBooking(bookingId);
        return new ResponseEntity<>("Booking deleted successfully", HttpStatus.OK);
    }
}
