package org.application.hotelbookingappbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.application.hotelbookingappbe.dto.BookingDto;
import org.application.hotelbookingappbe.dto.RoomDto;
import org.application.hotelbookingappbe.exception.GlobalExceptionHandler;
import org.application.hotelbookingappbe.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
    @WebMvcTest:
    Sadece web katmanını (Controller + MVC altyapısı) ayağa kaldırır
    Service/Repository gibi bağımlılıkları gerçek bean olarak yüklemez -> onları @MockBean ile mocklarız

    @Import(GlobalExceptionHandler.class):
    ControllerAdvice devreye girsin -> exception/validation response'ları test edelim

    @AutoConfigureMockMvc(addFilters = false)
    Security filtrelerini kapatıyoruz -> JWT testlerini ayrıca yazacağız
*/
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // SON serialize/deserialize için

    /*
        @MockBean:
        Spring context içine "mock bean" olarak eklenir
        Controller içindeki BookingService injection'ı bu mock üzerinden yapılır
    */
    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto1;
    private BookingDto bookingDto2;

    @BeforeEach
    void init() {
        // Creating Object With Builder
        bookingDto1 = BookingDto.builder()
                .bookingId(1L)
                .checkInDate(LocalDate.of(2026, 1, 10))
                .checkOutDate(LocalDate.of(2026, 1, 12))
                .guestName("Ahmet")
                .guestEmail("ahmet@gmail.com")
                .numOfAdults(2)
                .numOfChildren(1)
                .bookingConfirmationCode("CONF-123")
                .room(RoomDto.builder()
                        .id(10L)
                        .roomType("DELUXE")
                        .roomPrice(new BigDecimal(1500.00))
                        .build())
                .build();

        // Creating Object Without Builder
        bookingDto2 = new BookingDto();
        bookingDto2.setBookingId(2L);
        bookingDto2.setCheckInDate(LocalDate.of(2026, 2, 1));
        bookingDto2.setCheckOutDate(LocalDate.of(2026, 2, 3));
        bookingDto2.setGuestName("Mehmet");
        bookingDto2.setGuestEmail("mehmet@mail.com");
        bookingDto2.setNumOfAdults(3);
        bookingDto2.setNumOfChildren(0);
        bookingDto2.setBookingConfirmationCode("CONF-456");
        bookingDto2.setRoom(new RoomDto(20L, "SINGLE", new BigDecimal("2000.00")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBookings_shouldReturn200_andList() throws Exception {
        /*
            when: mock objenin davranışını belirleriz
            thenReturn: bu metod çağrıldığında dönmesini istediğimiz sonucu veririz
        */
        when(bookingService.getAllBookings()).thenReturn(List.of(bookingDto1, bookingDto2));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookingId", is(1)))
                .andExpect(jsonPath("$[0].guestEmail", is("ahmet@gmail.com")))
                .andExpect(jsonPath("$[1].bookingId", is(2)))
                .andExpect(jsonPath("$[1].guestEmail", is("mehmet@mail.com")));

        // verify: ilgili metod gerçekten kaç kez çağrıldı?
        verify(bookingService, times(1)).getAllBookings();
    }

    @Test
    void getBookingByConfirmationCode_shouldReturn200() throws Exception {
        when(bookingService.getBookingByConfirmationCode("CONF-123")).thenReturn(bookingDto1);

        mockMvc.perform(get("/api/bookings/confirmation-code/{confirmationCode}", "CONF-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingConfirmationCode", is("CONF-123")))
                .andExpect(jsonPath("$.guestName", is("Ahmet")));

        verify(bookingService).getBookingByConfirmationCode("CONF-123");
    }

    @Test
    void getBookingsByEmail_shouldReturn200() throws Exception {
        when(bookingService.getBookingsByEmail("ahmet@gmail.com")).thenReturn(List.of(bookingDto1));

        mockMvc.perform(get("/api/bookings/email/{email}", "ahmet@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.guestEmail[0]", is("ahmet@gmail.com")));

        verify(bookingService).getBookingsByEmail("ahmet@gmail.com");
    }

    @Test
    void addBooking_shouldReturn201_andCreatedDto() throws Exception {
        Long roomId = 10L;

        BookingDto requestDto = BookingDto.builder()
                .checkInDate(LocalDate.of(2026, 1, 10))
                .checkOutDate(LocalDate.of(2026, 1, 12))
                .guestName("Ahmet")
                .guestEmail("ahmet@mail.com")
                .numOfAdults(2)
                .numOfChildren(1)
                .build();

        /*
            any(): Parametrenin değerinin önemsenmemesidir. “Bu metod çağrılsın yeter, hangi nesneyle çağrıldığı önemli değil" anlamına gelir
            eq(value): "Bu parametre tam olarak bu değere eşit olmalı" anlamına gelir

            NOTE: Bir parametrede "matcher" kullanıldıysa, tüm parametrelerde matcher kullanılmalıdır:
            Yanlış: when(service.method(1L, any()))
            Doğru: when(service.method(eq(1L), any()))
        */
        when(bookingService.addBooking(eq(roomId), any(BookingDto.class))).thenReturn(bookingDto1);

        mockMvc.perform(post("/api/bookings/add-booking/{roomId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId", is(1)))
                .andExpect(jsonPath("$.bookingConfirmationCode", is("CONF-123")))
                .andExpect(jsonPath("$.room.id", is(10)));

        /*
            ArgumentCaptor: Metoda gönderilen gerçek parametreyi yakalar
            Controller JSON’dan DTO’ya map eder
            Service’e ne gönderdiğini test edersin
        */
        ArgumentCaptor<BookingDto> captor = ArgumentCaptor.forClass(BookingDto.class);

        verify(bookingService).addBooking(eq(roomId), captor.capture());

        BookingDto passedDto = captor.getValue();
        assertEquals("ahmet@mail.com", passedDto.getGuestEmail());
        assertEquals(2, passedDto.getNumOfAdults());
    }

    @Test
    void addBooking_whenValidationFails_shouldReturn400() throws Exception {
        Long roomId = 10L;

        BookingDto requestDto = BookingDto.builder()
                .checkInDate(LocalDate.of(2026, 1, 10))
                .checkOutDate(LocalDate.of(2026, 1, 12))
                .guestName("Ahmet")
                .guestEmail("")     // Invalid
                .numOfAdults(2)
                .numOfChildren(1)
                .build();

        mockMvc.perform(post("/api/bookings/add-booking/{roomId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.guestEmail", notNullValue()));   // GlobalExceptionHandler -> Map<String,String> dönüyor

        // Validation fail olduğunda service metodu hiç çağrılmamalı
        verifyNoInteractions(bookingService);
    }

    @Test
    void deleteBooking_shouldReturn200_andMessage() throws Exception {
        Long bookingId = 99L;

        // void metodlarda when(...).thenReturn() yerine doNothing/doThrow yaklaşımı kullanılır
        doNothing().when(bookingService).deleteBooking(bookingId);

        mockMvc.perform(delete("/api/bookings/delete-booking/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking deleted successfully"));

        verify(bookingService).deleteBooking(bookingId);
    }
}
