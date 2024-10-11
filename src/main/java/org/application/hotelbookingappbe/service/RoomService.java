package org.application.hotelbookingappbe.service;

import org.application.hotelbookingappbe.dto.RoomResponseDto;
import org.application.hotelbookingappbe.model.Room;
import org.application.hotelbookingappbe.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
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

    public List<RoomResponseDto> getRoomTypes() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream().map(this::mapToDto).toList();
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

        if (room.getPhoto() != null) {
            roomResponseDto.setPhoto(Base64.getEncoder().encodeToString(room.getPhoto()));
        } else {
            roomResponseDto.setPhoto(null);
        }

        return roomResponseDto;
    }


}
