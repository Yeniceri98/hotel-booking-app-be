package org.application.hotelbookingappbe.repository;

import org.application.hotelbookingappbe.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "SELECT DISTINCT room_type FROM room", nativeQuery = true)
    List<String> findDistinctRoomTypes();
}
