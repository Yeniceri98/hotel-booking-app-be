package org.application.hotelbookingappbe.service;

import org.application.hotelbookingappbe.dto.RoomDto;
import org.application.hotelbookingappbe.exception.RoomIsNotFoundException;
import org.application.hotelbookingappbe.model.Room;
import org.application.hotelbookingappbe.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Room room1;
    private Room room2;

    @BeforeEach
    void init() {
        room1 = Room.builder()
                .id(1L)
                .roomType("DELUXE")
                .roomPrice(new BigDecimal("1500.00"))
                .photo("img1".getBytes())
                .build();

        room2 = Room.builder()
                .id(2L)
                .roomType("STANDARD")
                .roomPrice(new BigDecimal("500.00"))
                .photo("img2".getBytes())
                .build();
    }

    @Test
    void addRoom_whenPhotoProvided_shouldSaveWithBytes() throws IOException {
        MultipartFile photo = mock(MultipartFile.class);

        when(photo.getBytes()).thenReturn("photo-bytes".getBytes());

        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room toSave = invocation.getArgument(0);
            toSave.setId(11L);
            return toSave;
        });

        RoomDto result = roomService.addRoom(photo, "DELUXE", new BigDecimal("1500.00"));

        assertEquals(11L, result.getId());
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void addRoom_whenPhotoNull_shouldSaveWithNullPhoto() throws IOException {
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room toSave = invocation.getArgument(0);
            toSave.setId(10L);
            return toSave;
        });

        RoomDto result = roomService.addRoom(null, "DELUXE", new BigDecimal("1500.00"));

        assertEquals(10L, result.getId());
        assertEquals("DELUXE", result.getRoomType());

        verify(roomRepository).save(any(Room.class));
    }

    @Test
    public void getRoomTypes_shouldReturnListOfRoomTypes() {
        List<String> roomTypes = List.of("DELUXE", "STANDARD");
        when(roomRepository.findDistinctRoomTypes()).thenReturn(roomTypes);

        List<String> result = roomService.getRoomTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("DELUXE"));
        assertTrue(result.contains("STANDARD"));

        verify(roomRepository).findDistinctRoomTypes();
    }

    @Test
    void getAllRooms_shouldReturnMappedDtos() {
        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        List<RoomDto> result = roomService.getAllRooms();

        assertEquals(2, result.size());
        assertEquals("DELUXE", result.get(0).getRoomType());
        assertEquals("STANDARD", result.get(1).getRoomType());

        verify(roomRepository).findAll();
    }

    @Test
    void getRoomById_whenFound_shouldReturnDto() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room1));

        RoomDto dto = roomService.getRoomById(1L);

        assertEquals(1L, dto.getId());
        assertEquals("DELUXE", dto.getRoomType());
        verify(roomRepository).findById(1L);
    }

    @Test
    void getRoomById_whenNotFound_shouldThrowRoomIsNotFoundException() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RoomIsNotFoundException.class,
                () -> roomService.getRoomById(99L)
        );

        verify(roomRepository).findById(99L);
    }

    @Test
    void getRoomPhotoByRoomId_shouldReturnBytes() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room1));

        byte[] result = roomService.getRoomPhotoByRoomId(1L);

        assertArrayEquals("img1".getBytes(), result);
        verify(roomRepository).findById(1L);
    }

    @Test
    void getAvailableRooms_shouldReturnRoomsNotInBookedRooms() {
        LocalDate in = LocalDate.of(2026, 1, 10);
        LocalDate out = LocalDate.of(2026, 1, 12);

        // bookedRoomsInDateRange -> room1 booked
        when(roomRepository.findBookedRoomsInDateRange(in, out, "DELUXE")).thenReturn(List.of(room1));

        // all rooms of type -> room1 + room2
        when(roomRepository.findByRoomType("DELUXE")).thenReturn(List.of(room1, room2));

        List<RoomDto> available = roomService.getAvailableRooms(in, out, "DELUXE");

        // room1 booked -> sadece room2 kalmalı
        assertEquals(1, available.size());
        assertEquals(2L, available.get(0).getId());

        verify(roomRepository).findBookedRoomsInDateRange(in, out, "DELUXE");
        verify(roomRepository).findByRoomType("DELUXE");
    }

    @Test
    void updateRoom_whenFoundAndPhotoProvided_shouldUpdateAndSave() throws IOException {
        MultipartFile photo = mock(MultipartFile.class);
        when(photo.getBytes()).thenReturn("new-photo".getBytes());

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room1));
        when(roomRepository.save(any(Room.class))).thenReturn(room1);

        RoomDto updated = roomService.updateRoom(1L, "DELUXE", new BigDecimal("1600.00"), photo);

        assertEquals(1L, updated.getId());
        assertEquals(new BigDecimal("1600.00"), updated.getRoomPrice());

        verify(roomRepository).findById(1L);
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void deleteRoom_whenFound_shouldDelete() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room1));

        doNothing().when(roomRepository).delete(room1);

        roomService.deleteRoom(1L);

        verify(roomRepository).findById(1L);
        verify(roomRepository).delete(room1);
    }

    @Test
    void deleteRoom_whenNotFound_shouldThrowRoomIsNotFoundException() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RoomIsNotFoundException.class,
                () -> roomService.deleteRoom(99L)
        );

        verify(roomRepository).findById(99L);
        verify(roomRepository, never()).delete(any());
    }
}
