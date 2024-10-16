package org.application.hotelbookingappbe.controller;

import org.application.hotelbookingappbe.dto.RoomResponseDto;
import org.application.hotelbookingappbe.service.BookingService;
import org.application.hotelbookingappbe.service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService, BookingService bookingService) {
        this.roomService = roomService;
    }

    @PostMapping("/add-room")
    public ResponseEntity<RoomResponseDto> addRoom(
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice
    ) throws IOException {
        return new ResponseEntity<>(roomService.addRoom(photo, roomType, roomPrice), HttpStatus.CREATED);
    }

    @GetMapping("/room-types")
    public ResponseEntity<List<String>> getRoomTypes() {
        return new ResponseEntity<>(roomService.getRoomTypes(), HttpStatus.OK);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Long roomId) {
        return new ResponseEntity<>(roomService.getRoomById(roomId), HttpStatus.OK);
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        return new ResponseEntity<>(roomService.getAllRooms(), HttpStatus.OK);
    }

    @GetMapping("/room-photo/{roomId}")
    public ResponseEntity<byte[]> getRoomPhotoByRoomId(@PathVariable Long roomId) {
        byte[] photo = roomService.getRoomPhotoByRoomId(roomId);

        if (photo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(photo, headers, HttpStatus.OK);
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponseDto>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam("roomType") String roomType
    ) {
        return new ResponseEntity<>(roomService.getAvailableRooms(checkInDate, checkOutDate, roomType), HttpStatus.OK);
    }
} 
