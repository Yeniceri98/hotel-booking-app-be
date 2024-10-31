package org.application.hotelbookingappbe.service;

import org.application.hotelbookingappbe.dto.RoomResponseDto;
import org.application.hotelbookingappbe.exception.RoomIsNotFoundException;
import org.application.hotelbookingappbe.model.Room;
import org.application.hotelbookingappbe.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public RoomResponseDto addRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) throws IOException {
        Room room = mapToEntity(photo, roomType, roomPrice);
        Room savedRoom = roomRepository.save(room);
        return mapToDto(savedRoom);
    }

    public List<String> getRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    public List<RoomResponseDto> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream().map(this::mapToDto).toList();
    }

    public RoomResponseDto getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RoomIsNotFoundException("Room is not found"));
        return mapToDto(room);
    }

    public Room getRoomEntityById(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new RoomIsNotFoundException("Room is not found"));
    }

    public byte[] getRoomPhotoByRoomId(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RoomIsNotFoundException("Room is not found"));
        return room.getPhoto();
    }

    public List<RoomResponseDto> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        List<Room> bookedRooms = roomRepository.findBookedRoomsInDateRange(checkInDate, checkOutDate, roomType);

        List<Room> allRooms = roomRepository.findByRoomType(roomType);

        List<Room> availableRooms = allRooms.stream().filter(room -> !bookedRooms.contains(room)).toList();

        return availableRooms.stream().map(this::mapToDto).toList();
    }

    public RoomResponseDto updateRoom(Long roomId, String roomType, BigDecimal roomPrice, MultipartFile photo) throws IOException {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RoomIsNotFoundException("Room is not found"));
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);

        if (photo != null && !photo.isEmpty()) {
            room.setPhoto(photo.getBytes());
        }

        Room updatedRoom = roomRepository.save(room);
        return mapToDto(updatedRoom);
    }

    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RoomIsNotFoundException("Room is not found"));
        roomRepository.delete(room);
    }

    private Room mapToEntity(MultipartFile photo, String roomType, BigDecimal roomPrice) throws IOException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);

        if (photo != null && !photo.isEmpty()) {
            room.setPhoto(photo.getBytes());
        } else {
            room.setPhoto(null);
        }

        return room;
    }

    private RoomResponseDto mapToDto(Room room) {
        RoomResponseDto roomResponseDto = new RoomResponseDto();
        roomResponseDto.setId(room.getId());
        roomResponseDto.setRoomType(room.getRoomType());
        roomResponseDto.setRoomPrice(room.getRoomPrice());

        return roomResponseDto;
    }
}
