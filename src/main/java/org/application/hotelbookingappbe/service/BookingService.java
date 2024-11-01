package org.application.hotelbookingappbe.service;

import org.application.hotelbookingappbe.dto.BookedRoomResponseDto;
import org.application.hotelbookingappbe.exception.BookingIsNotFoundException;
import org.application.hotelbookingappbe.exception.InvalidBookingRequestException;
import org.application.hotelbookingappbe.exception.RoomIsNotAvailableException;
import org.application.hotelbookingappbe.model.BookedRoom;
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

    public List<BookedRoomResponseDto> getAllBookings() {
        List<BookedRoom> bookedRooms = bookingRepository.findAll();

        if (bookedRooms.isEmpty()) {
            throw new BookingIsNotFoundException("Booking is not found");
        }

        return bookedRooms.stream().map(this::mapToDto).toList();
    }

    public BookedRoomResponseDto getBookingByConfirmationCode(String confirmationCode) {
        BookedRoom bookedRoom = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(
                () -> new BookingIsNotFoundException("Booking is not found with this booking confirmation code: " + confirmationCode));
        return mapToDto(bookedRoom);
    }

    public List<BookedRoomResponseDto> getBookingsByEmail(String email) {
        List<BookedRoom> bookedRooms = bookingRepository.findByGuestEmail(email);

        if (bookedRooms.isEmpty()) {
            throw new BookingIsNotFoundException("Booking is not found with this email: " + email);
        }

        return bookedRooms.stream().map(this::mapToDto).toList();
    }

    public BookedRoomResponseDto addBooking(Long roomId, BookedRoom bookedRoom) {
        if (bookedRoom.getCheckOutDate().isBefore(bookedRoom.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check out date cannot be before check in date");
        }

        Room room = roomService.getRoomEntityById(roomId);
        List<BookedRoom> existingBookings = room.getBookedRooms();

        if (!roomIsAvailable(bookedRoom, existingBookings)) {
            throw new RoomIsNotAvailableException("Room is not available for the selected dates");
        }

        room.addBooking(bookedRoom);
        BookedRoom savedBooking = bookingRepository.save(bookedRoom);
        return mapToDto(savedBooking);
    }

    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingIsNotFoundException("Booking is not found with this id: " + bookingId);
        }

        bookingRepository.deleteById(bookingId);
    }

    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
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

    private BookedRoom mapToEntity(BookedRoomResponseDto bookedRoomResponseDto) {
        BookedRoom bookedRoom = new BookedRoom();
        bookedRoom.setBookingId(bookedRoomResponseDto.getBookingId());
        bookedRoom.setCheckInDate(bookedRoomResponseDto.getCheckInDate());
        bookedRoom.setCheckOutDate(bookedRoomResponseDto.getCheckOutDate());
        bookedRoom.setBookingConfirmationCode(bookedRoomResponseDto.getBookingConfirmationCode());
        return bookedRoom;
    }

    private BookedRoomResponseDto mapToDto(BookedRoom bookedRoom) {
        BookedRoomResponseDto dto = new BookedRoomResponseDto();
        dto.setBookingId(bookedRoom.getBookingId());
        dto.setCheckInDate(bookedRoom.getCheckInDate());
        dto.setCheckOutDate(bookedRoom.getCheckOutDate());
        dto.setBookingConfirmationCode(bookedRoom.getBookingConfirmationCode());
        return dto;
    }
}
