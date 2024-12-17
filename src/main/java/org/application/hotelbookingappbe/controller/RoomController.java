package org.application.hotelbookingappbe.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.application.hotelbookingappbe.dto.RoomDto;
import org.application.hotelbookingappbe.service.RoomService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Tag(name = "Room Controller", description = "Room API")
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Tag(name = "Add Room")
    @PostMapping("/add-room")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomDto> addRoom(
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice
    ) throws IOException {
        return new ResponseEntity<>(roomService.addRoom(photo, roomType, roomPrice), HttpStatus.CREATED);
    }

    @Tag(name = "Get Room Types")
    @GetMapping("/room-types")
    public ResponseEntity<List<String>> getRoomTypes() {
        return new ResponseEntity<>(roomService.getRoomTypes(), HttpStatus.OK);
    }

    @Tag(name = "Get Room By Id")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId) {
        return new ResponseEntity<>(roomService.getRoomById(roomId), HttpStatus.OK);
    }

    @Tag(name = "Get All Rooms")
    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        return new ResponseEntity<>(roomService.getAllRooms(), HttpStatus.OK);
    }

    @Tag(name = "Get Room Photo By Room Id")
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

    @Tag(name = "Get Available Rooms")
    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomDto>> getAvailableRooms(
            @RequestParam("checkInDate") LocalDate checkInDate,
            @RequestParam("checkOutDate") LocalDate checkOutDate,
            @RequestParam("roomType") String roomType
    ) {
        return new ResponseEntity<>(roomService.getAvailableRooms(checkInDate, checkOutDate, roomType), HttpStatus.OK);
    }

    @Tag(name = "Update Room")
    @PutMapping("/room/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomDto> updateRoom(
            @PathVariable Long roomId,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice,
            @RequestParam(value = "photo", required = false) MultipartFile photo
    ) throws IOException {
        return new ResponseEntity<>(roomService.updateRoom(roomId, roomType, roomPrice, photo), HttpStatus.OK);
    }

    @Tag(name = "Delete Room")
    @DeleteMapping("/delete-room/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
