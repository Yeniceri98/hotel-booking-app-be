package org.application.hotelbookingappbe.controller;

import org.application.hotelbookingappbe.dto.RoomResponseDto;
import org.application.hotelbookingappbe.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/add-room")
    public ResponseEntity<RoomResponseDto> addRoom(
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice
    ) throws IOException, SQLException {
        return new ResponseEntity<>(roomService.addRoom(photo, roomType, roomPrice), HttpStatus.OK);
    }

    @GetMapping("/room-types")
    public ResponseEntity<List<RoomResponseDto>> getRoomTypes() {
        return new ResponseEntity<>(roomService.getRoomTypes(), HttpStatus.OK);
    }
}
