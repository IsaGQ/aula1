package com.hotel.demo.repository;

import com.hotel.demo.model.ReservaHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaHabitacionRepository extends JpaRepository<ReservaHabitacion, Long> {
}

