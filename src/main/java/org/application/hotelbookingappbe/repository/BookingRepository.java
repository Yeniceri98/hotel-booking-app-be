package org.application.hotelbookingappbe.repository;

import org.application.hotelbookingappbe.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookedRoom, Long> {
}
