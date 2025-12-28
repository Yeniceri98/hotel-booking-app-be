package org.application.hotelbookingappbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.application.hotelbookingappbe.dto.RoomDto;
import org.application.hotelbookingappbe.exception.GlobalExceptionHandler;
import org.application.hotelbookingappbe.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    private RoomDto roomDto1;
    private RoomDto roomDto2;

    @BeforeEach
    void init() {
        roomDto1 = RoomDto.builder()
                .id(1L)
                .roomType("DELUXE")
                .roomPrice(new BigDecimal("1500.00"))
                .build();
        roomDto2 = RoomDto.builder()
                .id(2L)
                .roomType("STANDARD")
                .roomPrice(new BigDecimal("900.00"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addRoom_shouldReturn201() throws Exception {
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "room.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        /*
            QUESTION: addRoom() metodunun ilk parametresinde neden "photo" yerine "any()" kullanıldı?
            - MultipartFile testin odak noktası değil. İçeriği byte[] olabilir. Dosya adı değişebilir. Farklı instance olabilir
            - Bu testte any() MultipartFile parametresi için kullanıldı çünkü dosyanın içeriği testin odağı değil; önemli olan metodun doğru business parametreleriyle çağrılmasıdır
            - any() diyerek herhangi bir MultipartFile üzerinden işlem yapılmış oluyor
        */
        when(roomService.addRoom(any(), eq("DELUXE"), eq(new BigDecimal("1500.00")))).thenReturn(roomDto1);

        // MockMultipartFile olduğu için "post" yerine "multipart" kullanıldı
        mockMvc.perform(multipart("/api/rooms/add-room")
                        .file(photo)
                        .param("roomType", "DELUXE")
                        .param("roomPrice", "1500.00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomType", is("DELUXE")))
                .andExpect(jsonPath("$.roomPrice", is(1500.00)));

        verify(roomService).addRoom(any(), eq("DELUXE"), eq(new BigDecimal("1500.00")));
    }

    @Test
    void getRoomTypes_shouldReturn200() throws Exception {
        when(roomService.getRoomTypes()).thenReturn(List.of("DELUXE", "STANDARD"));

        mockMvc.perform(get("/api/rooms/room-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("DELUXE")))
                .andExpect(jsonPath("$[1]", is("STANDARD")));

        // verify(roomService, times(1)).getRoomTypes(); ---> Alttakiyle aynıdır
        verify(roomService).getRoomTypes();
    }

    @Test
    void getRoomById_shouldReturn200() throws Exception {
        when(roomService.getRoomById(1L)).thenReturn(roomDto1);

        mockMvc.perform(get("/api/rooms/room/{roomId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.roomType", is("DELUXE")));

        verify(roomService).getRoomById(1L);
    }

    @Test
    void getAllRooms_shouldReturn200_andList() throws Exception {
        when(roomService.getAllRooms()).thenReturn(List.of(roomDto1, roomDto2));

        mockMvc.perform((get("/api/rooms/all-rooms")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].roomType", is("DELUXE")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].roomType", is("STANDARD")));

        verify(roomService).getAllRooms();
    }

    @Test
    void getRoomPhotoByRoomId_whenPhotoExists_shouldReturn200() throws Exception {
        byte[] photoBytes = "fake-image-content".getBytes();

        when(roomService.getRoomPhotoByRoomId(1L)).thenReturn(photoBytes);

        mockMvc.perform(get("/api/rooms/room-photo/{roomId}", 1L))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(photoBytes));

        verify(roomService).getRoomPhotoByRoomId(1L);
    }

    @Test
    void getAvailableRooms_shouldParseDatesWithInitBinder() throws Exception {
        when(roomService.getAvailableRooms(any(LocalDate.class), any(LocalDate.class), eq("DELUXE")))
                .thenReturn(List.of(roomDto1));

        mockMvc.perform(get("/api/rooms/available-rooms")
                        .param("checkInDate", "10-01-2026")
                        .param("checkOutDate", "12-01-2026")
                        .param("roomType", "DELUXE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].roomType", is("DELUXE")));

        verify(roomService).getAvailableRooms(
                eq(LocalDate.of(2026, 1, 10)),
                eq(LocalDate.of(2026, 1, 12)),
                eq("DELUXE")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoom_shouldReturn200() throws Exception {
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "room.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "updated-photo".getBytes()
        );

        when(roomService.updateRoom(eq(1L), eq("DELUXE"), eq(new BigDecimal("1600.00")), any()))
                .thenReturn(RoomDto.builder()
                        .id(1L)
                        .roomType("DELUXE")
                        .roomPrice(new BigDecimal("1600.00"))
                        .build());

        mockMvc.perform(multipart("/api/rooms/room/{roomId}", 1L)
                        .file(photo)
                        .param("roomType", "DELUXE")
                        .param("roomPrice", "1600.00")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomPrice", is(1600.00)));

        verify(roomService).updateRoom(eq(1L), eq("DELUXE"), eq(new BigDecimal("1600.00")), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRoom_shouldReturn200_andMessage() throws Exception {
        doNothing().when(roomService).deleteRoom(1L);

        mockMvc.perform(delete("/api/rooms/delete-room/{roomId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Room deleted successfully"));

        verify(roomService).deleteRoom(1L);
    }
}