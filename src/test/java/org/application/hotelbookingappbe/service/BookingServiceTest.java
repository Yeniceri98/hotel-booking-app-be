package org.application.hotelbookingappbe.service;

import org.application.hotelbookingappbe.dto.BookingDto;
import org.application.hotelbookingappbe.dto.RoomDto;
import org.application.hotelbookingappbe.exception.BookingIsNotFoundException;
import org.application.hotelbookingappbe.exception.InvalidBookingRequestException;
import org.application.hotelbookingappbe.exception.RoomIsNotAvailableException;
import org.application.hotelbookingappbe.model.Booking;
import org.application.hotelbookingappbe.model.Room;
import org.application.hotelbookingappbe.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private BookingDto bookingDto;
    private Room room;
    private RoomDto roomDto;

    @BeforeEach
    public void init() {
        booking = Booking.builder()
                .bookingId(1L)
                .checkInDate(LocalDate.parse("2023-06-01"))
                .checkOutDate(LocalDate.parse("2023-06-05"))
                .guestName("John Doe")
                .guestEmail("john.doe@example.com")
                .numOfAdults(1)
                .numOfChildren(1)
                .bookingConfirmationCode("ABC12")
                .room(room)
                .build();
        bookingDto = BookingDto.builder()
                .bookingId(1L)
                .checkInDate(LocalDate.parse("2023-06-01"))
                .checkOutDate(LocalDate.parse("2023-06-05"))
                .guestName("John Doe")
                .guestEmail("john.doe@example.com")
                .numOfAdults(1)
                .numOfChildren(1)
                .bookingConfirmationCode("ABC12")
                .room(roomDto)
                .build();
        room = Room.builder()
                .id(1L)
                .roomType("single room")
                .roomPrice(BigDecimal.valueOf(100.0))
                .isBooked(false)
                .photo(null)
                .bookings(new ArrayList<>())
                .build();
        roomDto = RoomDto.builder()
                .id(1L)
                .roomType("single room")
                .roomPrice(BigDecimal.valueOf(100.0))
                .build();
    }

    @Test
    public void getAllBookings_shouldReturnListOfBookingDtos() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDto> bookingDtos = bookingService.getAllBookings();

        assertNotNull(bookingDtos);
        assertEquals(1, bookingDtos.size());
        assertEquals(bookingDto.getBookingId(), bookingDtos.get(0).getBookingId());

        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    public void getBookingByConfirmationCode_shouldReturnBookingDto() {
        when(bookingRepository.findByBookingConfirmationCode("ABC12")).thenReturn(Optional.ofNullable(booking));

        BookingDto bookingDto = bookingService.getBookingByConfirmationCode("ABC12");

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getBookingId(), booking.getBookingId());

        verify(bookingRepository, times(1)).findByBookingConfirmationCode("ABC12");
    }

    @Test
    public void getBookingsByEmail_shouldReturnBookingDto() {
        when(bookingRepository.findByGuestEmail(booking.getGuestEmail())).thenReturn(List.of(booking));

        List<BookingDto> bookingDtos = bookingService.getBookingsByEmail(booking.getGuestEmail());

        assertNotNull(bookingDtos);
        assertEquals(1, bookingDtos.size());
        assertEquals(bookingDto.getBookingId(), bookingDtos.get(0).getBookingId());

        verify(bookingRepository, times(1)).findByGuestEmail(booking.getGuestEmail());
    }

    @Test
    public void getBookingsByEmail_shouldThrowException() {
        String email = "nonexistent@example.com";

        when(bookingRepository.findByGuestEmail(email)).thenReturn(Collections.emptyList());

        assertThrows(BookingIsNotFoundException.class, () -> {
            bookingService.getBookingsByEmail(email);
        });

        verify(bookingRepository, times(1)).findByGuestEmail(email);
    }

    @Test
    public void addBooking_shouldCreateAndReturnBookingDto() {
        Long roomId = 1L;

        // NOTE: bookingConfirmationCode ve room fieldları yollanmaz çünkü service'te addBooking() metodu içinde bu değerler otomatik oluşturuluyor
        BookingDto newBookingDto = BookingDto.builder()
                .checkInDate(LocalDate.parse("2023-07-01"))
                .checkOutDate(LocalDate.parse("2023-07-05"))
                .guestName("Jane Smith")
                .guestEmail("jane.smith@example.com")
                .numOfAdults(2)
                .numOfChildren(0)
                .build();

        Room availableRoom = Room.builder()
                .id(roomId)
                .roomType("double room")
                .roomPrice(BigDecimal.valueOf(150.0))
                .isBooked(false)
                .bookings(new ArrayList<>())
                .build();

        Booking newBooking = Booking.builder()
                .bookingId(2L)
                .checkInDate(newBookingDto.getCheckInDate())
                .checkOutDate(newBookingDto.getCheckOutDate())
                .guestName(newBookingDto.getGuestName())
                .guestEmail(newBookingDto.getGuestEmail())
                .numOfAdults(newBookingDto.getNumOfAdults())
                .numOfChildren(newBookingDto.getNumOfChildren())
                .bookingConfirmationCode("ABC123")
                .room(availableRoom)
                .build();

        when(roomService.getRoomEntityById(roomId)).thenReturn(availableRoom);
        when(bookingRepository.save(any(Booking.class))).thenReturn(newBooking);

        BookingDto result = bookingService.addBooking(roomId, newBookingDto);

        assertNotNull(result);
        assertEquals(newBooking.getBookingId(), result.getBookingId());
        assertEquals(newBooking.getGuestName(), result.getGuestName());
        assertEquals(newBooking.getBookingConfirmationCode(), result.getBookingConfirmationCode());

        verify(roomService, times(1)).getRoomEntityById(roomId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void addBooking_withInvalidDates_shouldThrowException() {
        // Given
        Long roomId = 1L;
        BookingDto invalidBookingDto = BookingDto.builder()
                .checkInDate(LocalDate.parse("2023-07-05"))  // Check-in after check-out
                .checkOutDate(LocalDate.parse("2023-07-01"))
                .guestName("Jane Smith")
                .guestEmail("jane.smith@example.com")
                .numOfAdults(2)
                .numOfChildren(0)
                .build();

        // When & Then
        assertThrows(InvalidBookingRequestException.class, () -> {
            bookingService.addBooking(roomId, invalidBookingDto);
        });

        // Verify that repository methods were not called
        verify(roomService, never()).getRoomEntityById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void addBooking_whenRoomNotAvailable_shouldThrowException() {
        // Given
        Long roomId = 1L;

        // Make sure room has a properly initialized bookings list
        if (room.getBookings() == null) {
            room.setBookings(new ArrayList<>());
        }

        // Ensure the existing booking is in the room's bookings list
        if (!room.getBookings().contains(booking)) {
            room.getBookings().add(booking);
        }

        // Create a booking with dates that overlap with existing booking
        BookingDto overlappingBookingDto = BookingDto.builder()
                .checkInDate(LocalDate.parse("2023-06-03"))  // Overlaps with existing booking (2023-06-01 to 2023-06-05)
                .checkOutDate(LocalDate.parse("2023-06-07"))
                .guestName("Jane Smith")
                .guestEmail("jane.smith@example.com")
                .numOfAdults(2)
                .numOfChildren(0)
                .build();

        when(roomService.getRoomEntityById(roomId)).thenReturn(room);

        // When & Then
        assertThrows(RoomIsNotAvailableException.class, () -> {
            bookingService.addBooking(roomId, overlappingBookingDto);
        });

        verify(roomService, times(1)).getRoomEntityById(roomId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    @Test
    public void deleteBooking_shouldDeleteSuccessfully() {
        // Given
        Long bookingId = 1L;
        when(bookingRepository.existsById(bookingId)).thenReturn(true);
        doNothing().when(bookingRepository).deleteById(bookingId);

        // When
        bookingService.deleteBooking(bookingId);

        // Then
        verify(bookingRepository, times(1)).existsById(bookingId);
        verify(bookingRepository, times(1)).deleteById(bookingId);
    }

    @Test
    public void deleteBooking_whenBookingNotFound_shouldThrowException() {
        // Given
        Long nonExistentId = 999L;
        when(bookingRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThrows(BookingIsNotFoundException.class, () -> {
            bookingService.deleteBooking(nonExistentId);
        });

        verify(bookingRepository, times(1)).existsById(nonExistentId);
        verify(bookingRepository, never()).deleteById(anyLong());
    }
}
