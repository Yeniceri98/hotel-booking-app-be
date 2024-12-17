package org.application.hotelbookingappbe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long bookingId;

    @NotBlank(message = "Check-in date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate checkInDate;

    @NotBlank(message = "Check-out date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate checkOutDate;

    @NotBlank(message = "Guest name is required")
    @Size(min = 2, max = 15, message = "Guest name must be between 2 and 15 characters long")
    private String guestName;

    @NotBlank(message = "Guest email is required")
    @Email(message = "Invalid email format")
    private String guestEmail;

    @Min(value = 1, message = "Number of adults must be at least 1")
    private Integer numOfAdults;

    @Positive(message = "Number of children value cannot be negative")
    private Integer numOfChildren;

    private String bookingConfirmationCode;
    private RoomDto room;
}
