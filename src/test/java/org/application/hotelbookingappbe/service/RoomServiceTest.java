package org.application.hotelbookingappbe.service;

import org.application.hotelbookingappbe.dto.RoomDto;
import org.application.hotelbookingappbe.exception.RoomIsNotFoundException;
import org.application.hotelbookingappbe.model.Room;
import org.application.hotelbookingappbe.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
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

    private Room room;
    private RoomDto roomDto;

    @BeforeEach
    public void init() {
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
    public void addRoom_shouldReturnRoomDto() throws IOException {
        // Given
        MultipartFile photo = mock(MultipartFile.class);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        // When
        RoomDto savedRoomDto = roomService.addRoom(photo, "single room", BigDecimal.valueOf(100.0));

        // Then
        assertNotNull(savedRoomDto);
        assertEquals(roomDto.getId(), savedRoomDto.getId());
        assertEquals(roomDto.getRoomType(), savedRoomDto.getRoomType());
        assertEquals(roomDto.getRoomPrice(), savedRoomDto.getRoomPrice());
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    public void getRoomTypes_shouldReturnListOfRoomTypes() {
        // Given
        List<String> roomTypes = List.of("single room", "double room");
        when(roomRepository.findDistinctRoomTypes()).thenReturn(roomTypes);

        // When
        List<String> result = roomService.getRoomTypes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("single room"));
        assertTrue(result.contains("double room"));
        verify(roomRepository, times(1)).findDistinctRoomTypes();
    }

    @Test
    public void getAllRooms_shouldReturnListOfRoomDto() {
        when(roomRepository.findAll()).thenReturn(List.of(room));

        List<RoomDto> result = roomService.getAllRooms();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(room.getRoomType(), result.get(0).getRoomType());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    public void getRoomById_shouldReturnRoomDto() {
        when(roomRepository.findById(room.getId())).thenReturn(Optional.ofNullable(room));

        RoomDto result = roomService.getRoomById(room.getId());

        assertNotNull(result);
        assertEquals(room.getId(), result.getId());
        assertEquals(room.getRoomType(), result.getRoomType());
        verify(roomRepository, times(1)).findById(room.getId());
    }

    @Test
    public void getRoomByIdNotFound_shouldReturnException() {
        when(roomRepository.findById(room.getId())).thenReturn(Optional.empty());   // Simulate not found

        assertThrows(RoomIsNotFoundException.class, () -> {
            roomService.getRoomById(room.getId());
        });

        verify(roomRepository, times(1)).findById(room.getId());
    }

    @Test
    public void getRoomPhotoByRoomId_shouldReturnRoomPhoto() {
        byte[] photoData = new byte[]{1, 2, 3};
        room.setPhoto(photoData);
        when(roomRepository.findById(room.getId())).thenReturn(Optional.ofNullable(room));

        byte[] result = roomService.getRoomPhotoByRoomId(room.getId());

        assertNotNull(result);
        assertArrayEquals(photoData, result);
        verify(roomRepository, times(1)).findById(room.getId());
    }

    @Test
    public void getAvailableRooms_shouldReturnListOfRoomDto() {
        List<Room> bookedRooms = List.of(room);
        List<Room> allRooms = List.of(room);
        when(roomRepository.findBookedRoomsInDateRange(any(), any(), any())).thenReturn(bookedRooms);
        when(roomRepository.findByRoomType("single room")).thenReturn(allRooms);

        List<RoomDto> availableRooms = roomService.getAvailableRooms(null, null, "single room");

        assertNotNull(availableRooms);
        assertTrue(availableRooms.isEmpty());
        verify(roomRepository, times(1)).findBookedRoomsInDateRange(any(), any(), any());
        verify(roomRepository, times(1)).findByRoomType("single room");
    }

    @Test
    public void updateRoom_shouldReturnUpdatedRoomDto() throws IOException {
        MultipartFile photo = mock(MultipartFile.class);
        when(roomRepository.findById(room.getId())).thenReturn(Optional.ofNullable(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomDto updatedRoom = roomService.updateRoom(room.getId(), "double suit", BigDecimal.valueOf(150.0), photo);

        assertNotNull(updatedRoom);
        assertEquals(1L, updatedRoom.getId());
        assertEquals("double suit", updatedRoom.getRoomType());
        assertEquals(BigDecimal.valueOf(150.0), updatedRoom.getRoomPrice());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    public void deleteRoom_shouldDeleteExistingRoom() {
        when(roomRepository.findById(room.getId())).thenReturn(Optional.ofNullable(room));

        roomService.deleteRoom(room.getId());

        verify(roomRepository, times(1)).delete(room);
    }
}
