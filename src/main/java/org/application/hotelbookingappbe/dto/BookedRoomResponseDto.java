package org.application.hotelbookingappbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookedRoomResponseDto {
    private Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String bookingConfirmationCode;
}
