package org.application.hotelbookingappbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BookedRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

}
