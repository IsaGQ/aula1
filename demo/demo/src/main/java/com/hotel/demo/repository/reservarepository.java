/*package com.hotel.demo.repository;

import com.hotel.demo.model.reservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface reservarepository extends JpaRepository<reservacion, Long> {
    Optional<reservacion> findByUsuarioIdAndConfirmadaFalse(Long usuarioId);
    List<reservacion> findByUsuarioId(Long usuarioId);
    List<reservacion> findByEstado(String estado);
}*/
package com.hotel.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.demo.model.reservacion;

@Repository

public interface reservarepository extends JpaRepository<reservacion, Long> {
    boolean existsByHabitaciones_Id(Long idHabitacion);
}
