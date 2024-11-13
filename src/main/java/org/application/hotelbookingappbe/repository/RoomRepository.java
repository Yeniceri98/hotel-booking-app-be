package org.application.hotelbookingappbe.repository;

import org.application.hotelbookingappbe.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "SELECT DISTINCT room_type FROM room", nativeQuery = true)
    List<String> findDistinctRoomTypes();

    @Query(value = "SELECT br.room FROM Booking br WHERE br.checkInDate <= :checkInDate AND br.checkOutDate >= :checkOutDate AND br.room.roomType = :roomType")
    List<Room> findBookedRoomsInDateRange(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    List<Room> findByRoomType(String roomType);
}
