package org.application.hotelbookingappbe.controller;

import org.application.hotelbookingappbe.dto.RoomDto;
import org.application.hotelbookingappbe.service.RoomService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/add-room")
    public ResponseEntity<RoomDto> addRoom(
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
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId) {
        return new ResponseEntity<>(roomService.getRoomById(roomId), HttpStatus.OK);
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomDto>> getAllRooms() {
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
    public ResponseEntity<List<RoomDto>> getAvailableRooms(
            @RequestParam("checkInDate") LocalDate checkInDate,
            @RequestParam("checkOutDate") LocalDate checkOutDate,
            @RequestParam("roomType") String roomType
    ) {
        return new ResponseEntity<>(roomService.getAvailableRooms(checkInDate, checkOutDate, roomType), HttpStatus.OK);
    }

    @PutMapping("/room/{roomId}")
    public ResponseEntity<RoomDto> updateRoom(
            @PathVariable Long roomId,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice,
            @RequestParam(value = "photo", required = false) MultipartFile photo
    ) throws IOException {
        return new ResponseEntity<>(roomService.updateRoom(roomId, roomType, roomPrice, photo), HttpStatus.OK);
    }

    @DeleteMapping("/delete-room/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>("Room deleted successfully", HttpStatus.OK);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(LocalDate.parse(text, formatter));
            }
        });
    }
} 
