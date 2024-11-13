package org.application.hotelbookingappbe.service;

import org.application.hotelbookingappbe.dto.BookingDto;
import org.application.hotelbookingappbe.dto.RoomDto;
import org.application.hotelbookingappbe.exception.BookingIsNotFoundException;
import org.application.hotelbookingappbe.exception.InvalidBookingRequestException;
import org.application.hotelbookingappbe.exception.RoomIsNotAvailableException;
import org.application.hotelbookingappbe.model.Booking;
import org.application.hotelbookingappbe.model.Room;
import org.application.hotelbookingappbe.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    public BookingService(BookingRepository bookingRepository, RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
    }

    public List<BookingDto> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        if (bookings.isEmpty()) {
            throw new BookingIsNotFoundException("Booking is not found");
        }

        return bookings.stream().map(this::mapToDto).toList();
    }

    public BookingDto getBookingByConfirmationCode(String confirmationCode) {
        Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(
                () -> new BookingIsNotFoundException("Booking is not found with this booking confirmation code: " + confirmationCode));
        return mapToDto(booking);
    }

    public List<BookingDto> getBookingsByEmail(String email) {
        List<Booking> bookings = bookingRepository.findByGuestEmail(email);

        if (bookings.isEmpty()) {
            throw new BookingIsNotFoundException("Booking is not found with this email: " + email);
        }

        return bookings.stream().map(this::mapToDto).toList();
    }

    public BookingDto addBooking(Long roomId, BookingDto bookingDto) {
        Booking booking = mapToEntity(bookingDto);

        if (booking.getCheckOutDate().isBefore(booking.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check out date cannot be before check in date");
        }

        Room room = roomService.getRoomEntityById(roomId);
        List<Booking> existingBookings = room.getBookings();

        if (!roomIsAvailable(booking, existingBookings)) {
            throw new RoomIsNotAvailableException("Room is not available for the selected dates");
        }

        room.addBooking(booking);
        Booking savedBooking = bookingRepository.save(booking);

        return mapToDto(savedBooking);
    }

    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingIsNotFoundException("Booking is not found with this id: " + bookingId);
        }

        bookingRepository.deleteById(bookingId);
    }

    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }

    private Booking mapToEntity(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setGuestName(bookingDto.getGuestName());
        booking.setGuestEmail(bookingDto.getGuestEmail());
        booking.setNumOfAdults(bookingDto.getNumOfAdults());
        booking.setNumOfChildren(bookingDto.getNumOfChildren());
        return booking;
    }

    private BookingDto mapToDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setBookingId(booking.getBookingId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setGuestName(booking.getGuestName());
        dto.setGuestEmail(booking.getGuestEmail());
        dto.setNumOfAdults(booking.getNumOfAdults());
        dto.setNumOfChildren(booking.getNumOfChildren());
        dto.setBookingConfirmationCode(booking.getBookingConfirmationCode());

        if (booking.getRoom() != null) {
            RoomDto roomDto = new RoomDto();
            roomDto.setId(booking.getRoom().getId());
            roomDto.setRoomType(booking.getRoom().getRoomType());
            roomDto.setRoomPrice(booking.getRoom().getRoomPrice());
            dto.setRoom(roomDto);
        }

        return dto;
    }
}
