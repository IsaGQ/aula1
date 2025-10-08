package com.hotel.demo.repository;

import com.hotel.demo.model.reservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
import java.util.List;
import java.util.Optional;

@Repository
public interface reservarepository extends JpaRepository<reservacion, Long> {
    Optional<reservacion> findByUsuarioIdAndConfirmadaFalse(Long usuarioId);
    List<reservacion> findByUsuarioId(Long usuarioId);
    List<reservacion> findByEstado(String estado);
=======
import com.hotel.demo.model.Reservacion;

@Repository

public interface ReservaRepository extends JpaRepository<Reservacion, Long> {
    boolean existsByHabitaciones_Id(Long idHabitacion);
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
}
