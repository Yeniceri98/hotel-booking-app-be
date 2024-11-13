package org.application.hotelbookingappbe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long bookingId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate checkInDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate checkOutDate;

    private String guestName;
    private String guestEmail;
    private Integer numOfAdults;
    private Integer numOfChildren;
    private String bookingConfirmationCode;
    private RoomDto room;

}
