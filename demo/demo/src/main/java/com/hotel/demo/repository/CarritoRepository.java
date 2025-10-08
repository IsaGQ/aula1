package com.hotel.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel.demo.model.Carrito;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}
