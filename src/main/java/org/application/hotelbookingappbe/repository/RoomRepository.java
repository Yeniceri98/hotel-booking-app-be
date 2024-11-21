package org.application.hotelbookingappbe.repository;

import org.application.hotelbookingappbe.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "SELECT DISTINCT room_type FROM room", nativeQuery = true)
    List<String> findDistinctRoomTypes();

    @Query(" SELECT r FROM Room r " +
            " WHERE r.roomType LIKE %:roomType% " +
            " AND r.id NOT IN (" +
            "  SELECT br.room.id FROM Booking br " +
            "  WHERE ((br.checkInDate <= :checkOutDate) AND (br.checkOutDate >= :checkInDate))" +
            ")")
    List<Room> findBookedRoomsInDateRange(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    List<Room> findByRoomType(String roomType);
}
