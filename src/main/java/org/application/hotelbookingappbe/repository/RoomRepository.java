package org.application.hotelbookingappbe.repository;

import org.application.hotelbookingappbe.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
