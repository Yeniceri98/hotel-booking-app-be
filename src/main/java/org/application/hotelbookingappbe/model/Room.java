package org.application.hotelbookingappbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String roomType;
    private BigDecimal roomPrice;
    private Boolean isBooked = false;

    @Lob
    private Blob photo;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "room"
    )
    private List<BookedRoom> bookedRooms;

    public void addBooking(BookedRoom bookedRoom) {
        if (bookedRooms == null) {
            bookedRooms = new ArrayList<>();
        }

        bookedRooms.add(bookedRoom);
        bookedRoom.setRoom(this);

        isBooked = true;

        String confirmationCode = UUID.randomUUID().toString();
        bookedRoom.setBookingConfirmationCode(confirmationCode);
    }
}
