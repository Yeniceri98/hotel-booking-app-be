package org.application.hotelbookingappbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BookedRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookingId;

    @Column(name = "check_in")
    private LocalDate checkInDate;

    @Column(name = "check_out")
    private LocalDate checkOutDate;

    private String guestFullName;
    private String guestEmail;

    @Column(name = "adults")
    private Integer numOfAdults;

    @Column(name = "children")
    private Integer numOfChildren;

    @Column(name = "total_guests")
    private Integer totalNumOfGuests;

    @Column(name = "confirmation_code")
    private String bookingConfirmationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public Integer calculateNumOfGuests() {
        totalNumOfGuests = numOfAdults + numOfChildren;
        return totalNumOfGuests;
    }
}
