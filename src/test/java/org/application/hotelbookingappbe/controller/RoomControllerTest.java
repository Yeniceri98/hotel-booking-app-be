package org.application.hotelbookingappbe.controller;

import org.application.hotelbookingappbe.dto.RoomDto;
import org.application.hotelbookingappbe.model.Room;
import org.application.hotelbookingappbe.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@EnableMethodSecurity
@WithMockUser(roles = "ADMIN")
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    private Room room;
    private RoomDto roomDto;

    @BeforeEach
    void setup() {
        room = Room.builder()
                .id(1L)
                .roomType("single room")
                .roomPrice(BigDecimal.valueOf(100.0))
                .build();
        roomDto = RoomDto.builder()
                .roomType("single room")
                .roomPrice(BigDecimal.valueOf(100.0))
                .build();
    }

    @Test
    void addRoom_shouldCreateRoom() throws Exception {
        // Arrange
        when(roomService.addRoom(any(), anyString(), any())).thenReturn(roomDto);

        // Act & Assert
        mockMvc.perform(post("/api/rooms/add-room")
                        .contentType(MediaType.MULTIPART_FORM_DATA)     // Request has file
                        .param("roomType", "single room")               // Parameter check in request
                        .param("roomPrice", "100.0")
                )

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomType").value("single room"));    // Expected response for this param

        // Verify
        verify(roomService, times(1)).addRoom(any(), anyString(), any());
    }

    @Test
    void getRoomTypes_shouldReturnListOfRoomTypes() throws Exception {
        List<String> roomTypes = List.of("single room", "double room", "suite");
        when(roomService.getRoomTypes()).thenReturn(roomTypes);

        mockMvc.perform(get("/api/rooms/room-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]").value("single room"))
                .andExpect(jsonPath("$[1]").value("double room"))
                .andExpect(jsonPath("$[2]").value("suite"));
    }

    @Test
    void getRoomById_shouldReturnRoomDto() throws Exception {
        when(roomService.getRoomById(room.getId())).thenReturn(roomDto);

        mockMvc.perform(get("/api/rooms/room/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllRooms_shouldReturnRoomDto() throws Exception {
        when(roomService.getAllRooms()).thenReturn(List.of(roomDto));

        mockMvc.perform(get("/api/rooms/all-rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].roomType").value("single room"))
                .andExpect(jsonPath("$[0].roomPrice").value(100.0));
    }
}