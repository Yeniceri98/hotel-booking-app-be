package org.application.hotelbookingappbe.service;

import org.application.hotelbookingappbe.dto.BookingDto;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    /*
        @Mock:
        BookingService'in ihtiyaç duyduğu bağımlılıkları mock etmek için kullanılır
        Bu anotasyon sayesinde BookingRepository ve RoomService mocklanmış olur
        Çünkü Unit Test'te gerçek DB/Repository davranışı istemiyoruz
    */
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomService roomService;

    /*
        @InjectMocks:
        Test etmek istenilen class için kullanılır
        Mockito, yukarıdaki mockları (repo/service) constructor'a enjekte eder
    */
    @InjectMocks
    private BookingService bookingService;

    private Booking booking1;
    private Booking booking2;
    private Room room;

    @BeforeEach
    void init() {
        room = Room.builder()
                .id(10L)
                .roomType("DELUXE")
                .roomPrice(new BigDecimal("1500.00"))
                .bookings(new ArrayList<>())
                .build();

        booking1 = Booking.builder()
                .bookingId(1L)
                .checkInDate(LocalDate.of(2026, 1, 10))
                .checkOutDate(LocalDate.of(2026, 1, 12))
                .guestName("Ahmet")
                .guestEmail("ahmet@mail.com")
                .numOfAdults(2)
                .numOfChildren(0)
                .bookingConfirmationCode("CONF-123")
                .room(room)
                .build();

        booking2 = Booking.builder()
                .bookingId(2L)
                .checkInDate(LocalDate.of(2026, 2, 10))
                .checkOutDate(LocalDate.of(2026, 2, 12))
                .guestName("Mehmet")
                .guestEmail("mehmet@mail.com")
                .numOfAdults(3)
                .numOfChildren(1)
                .bookingConfirmationCode("CONF-456")
                .room(room)
                .build();
    }

    @Test
    void getAllBookings_whenFound_shouldReturnDtos() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking1, booking2));

        List<BookingDto> bookingDtos = bookingService.getAllBookings();

        assertNotNull(bookingDtos);
        assertEquals("CONF-123", bookingDtos.get(0).getBookingConfirmationCode());
        assertEquals("mehmet@mail.com", bookingDtos.get(1).getGuestEmail());

        // verify(bookingRepository, times(1)).findAll();  --->  Alttakiyle aynıdır
        verify(bookingRepository).findAll();
    }

    @Test
    void getAllBookings_whenEmpty_shouldThrowBookingIsNotFoundException() {
        when(bookingRepository.findAll()).thenReturn(List.of());

        assertThrows(
                BookingIsNotFoundException.class,
                () -> bookingService.getAllBookings()
        );

        verify(bookingRepository).findAll();
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getBookingByConfirmationCode_whenFound_shouldReturnDto() {
        when(bookingRepository.findByBookingConfirmationCode("CONF-123"))
                .thenReturn(Optional.ofNullable(booking1));

        BookingDto bookingDto = bookingService.getBookingByConfirmationCode("CONF-123");

        assertEquals("Ahmet", bookingDto.getGuestName());
        assertEquals(10L, bookingDto.getRoom().getId());

        verify(bookingRepository).findByBookingConfirmationCode("CONF-123");
    }

    @Test
    void getBookingByConfirmationCode_whenNotFound_shouldThrowBookingIsNotFoundException() {
        when(bookingRepository.findByBookingConfirmationCode("XXX")).thenReturn(Optional.empty());

        assertThrows(
                BookingIsNotFoundException.class,
                () -> bookingService.getBookingByConfirmationCode("XXX")
        );

        verify(bookingRepository).findByBookingConfirmationCode("XXX");
    }

    @Test
    void getBookingsByEmail_whenFound_shouldReturnDtoList() {
        when(bookingRepository.findByGuestEmail("ahmet@gmail.com")).thenReturn(List.of(booking1));

        List<BookingDto> bookingDtos = bookingService.getBookingsByEmail("ahmet@gmail.com");

        assertEquals(1, bookingDtos.size());
        assertEquals("CONF-123", bookingDtos.get(0).getBookingConfirmationCode());
        assertEquals("Ahmet", bookingDtos.get(0).getGuestName());

        verify(bookingRepository).findByGuestEmail("ahmet@gmail.com");
    }

    @Test
    void getBookingsByEmail_whenEmpty_shouldThrowBookingIsNotFoundException() {
        when(bookingRepository.findByGuestEmail("none@mail.com")).thenReturn(List.of());

        assertThrows(
                BookingIsNotFoundException.class,
                () -> bookingService.getBookingsByEmail("none@mail.com")
        );

        verify(bookingRepository).findByGuestEmail("none@mail.com");
    }

    @Test
    void addBooking_whenAvailable_shouldSaveAndReturnDto() {
        BookingDto requestDto = BookingDto.builder()
                .checkInDate(LocalDate.of(2026, 3, 10))
                .checkOutDate(LocalDate.of(2026, 3, 12))
                .guestName("Ahmet")
                .guestEmail("ahmet@mail.com")
                .numOfAdults(2)
                .numOfChildren(0)
                .build();

        Room emptyRoom = Room.builder()
                .id(10L)
                .roomType("DELUXE")
                .roomPrice(new BigDecimal("1500.00"))
                .bookings(new ArrayList<>())
                .build();

        when(roomService.getRoomEntityById(10L)).thenReturn(emptyRoom);

        /*
            Mockito Answer & thenAnswer:
            Stub edilen metodun dönüşünü çağrı anındaki parametreye göre dinamik üretmek gerektiğinde kullanılır
            Özellikle repository save davranışını simüle etmek için idealdir
            Gerçekten DB, save() sonrası ID set eder
            save(...) çağrılınca kaydedilmiş gibi geri dönmek için Answer kullanıyoruz
            Böylece service dönüşündeki mapping'i test edebiliriz

            thenReturn vs thenAnswer
            - thenReturn: Sabit bir değer döner. Parametreler dikkate alınmaz. Basit senaryolar için yeterlidir
            - thenAnswer: Parametreye bakar. Dinamik dönüş üretir. Gerçek davranışı simüle eder
        */
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking toSave = invocation.getArgument(0);
            toSave.setBookingId(999L); // DB id set etti varsayalım
            return toSave;
        });

        BookingDto result = bookingService.addBooking(10L, requestDto);

        assertEquals(999L, result.getBookingId());
        assertEquals("Ahmet", result.getGuestName());
        assertNotNull(result.getBookingConfirmationCode()); // Room.addBooking(...) UUID üretir

        verify(roomService).getRoomEntityById(10L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void addBooking_whenCheckoutDateIsBeforeCheckinDate_shouldThrowInvalidBookingRequestException() {
        BookingDto requestDto = BookingDto.builder()
                .checkInDate(LocalDate.of(2026, 1, 12))
                .checkOutDate(LocalDate.of(2026, 1, 10)) // invalid
                .guestName("Ahmet")
                .guestEmail("ahmet@mail.com")
                .numOfAdults(2)
                .numOfChildren(0)
                .build();

        assertThrows(
                InvalidBookingRequestException.class,
                () -> bookingService.addBooking(10L, requestDto)
        );

        // Bu senaryoda RoomService hiç çağrılmamalı (date validation daha önce patlıyor)
        verifyNoInteractions(roomService);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void addBooking_whenRoomNotAvailable_shouldThrowRoomIsNotAvailableException_andNotSave() {
        BookingDto requestDto = BookingDto.builder()
                .checkInDate(LocalDate.of(2026, 1, 10))
                .checkOutDate(LocalDate.of(2026, 1, 12))
                .guestName("Ahmet")
                .guestEmail("ahmet@mail.com")
                .numOfAdults(2)
                .numOfChildren(0)
                .build();

        // Room üzerinde aynı checkInDate'e sahip bir booking varsa availability false'a düşer
        Room roomWithExistingBooking = Room.builder()
                .id(10L)
                .roomType("DELUXE")
                .roomPrice(new BigDecimal("1500.00"))
                .bookings(new ArrayList<>(List.of(
                        Booking.builder()
                                .checkInDate(LocalDate.of(2026, 1, 10))
                                .checkOutDate(LocalDate.of(2026, 1, 15))
                                .build()
                )))
                .build();

        when(roomService.getRoomEntityById(10L)).thenReturn(roomWithExistingBooking);

        assertThrows(
                RoomIsNotAvailableException.class,
                () -> bookingService.addBooking(10L, requestDto)
        );

        verify(roomService).getRoomEntityById(10L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void deleteBooking_whenExists_shouldDelete() {
        when(bookingRepository.existsById(55L)).thenReturn(true);

        doNothing().when(bookingRepository).deleteById(55L);

        bookingService.deleteBooking(55L);

        verify(bookingRepository).existsById(55L);
        verify(bookingRepository).deleteById(55L);
    }

    @Test
    void deleteBooking_whenNotExists_shouldThrowBookingIsNotFoundException_andNotDelete() {
        when(bookingRepository.existsById(55L)).thenReturn(false);

        assertThrows(BookingIsNotFoundException.class, () -> bookingService.deleteBooking(55L));

        verify(bookingRepository).existsById(55L);
        verify(bookingRepository, never()).deleteById(anyLong());
    }
}
