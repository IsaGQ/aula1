package com.hotel.demo.controller;
import com.hotel.demo.model.reservacion;
import com.hotel.demo.service.ReservacionService;
import com.hotel.demo.repository.reservarepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservaciones")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminReservacionController {

    private final reservarepository reservacionRepository;
    public AdminReservacionController(reservarepository reservacionRepository, ReservacionService reservacionService) {
        this.reservacionRepository = reservacionRepository;
    }

    @GetMapping
    public List<reservacion> listarTodas() {
        return reservacionRepository.findAll();
    }

    @GetMapping("/{id}")
    public reservacion detalle(@PathVariable Long id) {
        return reservacionRepository.findById(id).orElseThrow(() -> new RuntimeException("Reservación no encontrada"));
    }

    @PostMapping
    public reservacion crear(@RequestBody reservacion r) {
        // En admin permitimos crear/forzar reservas
        return reservacionRepository.save(r);
    }

    @PutMapping("/{id}")
    public reservacion editar(@PathVariable Long id, @RequestBody reservacion datos) {
        reservacion existente = reservacionRepository.findById(id).orElseThrow(() -> new RuntimeException("Reservación no encontrada"));
        existente.setFechaLlegada(datos.getFechaLlegada());
        existente.setFechaSalida(datos.getFechaSalida());
        existente.setPrecioTotal(datos.getPrecioTotal());
        existente.setEstado(datos.getEstado());
        existente.setNombreCompleto(datos.getNombreCompleto());
        existente.setCorreo(datos.getCorreo());
        existente.setDireccion(datos.getDireccion());
        existente.setCelular(datos.getCelular());
        // No tocamos items aquí (puedes exponer endpoints para eso)
        return reservacionRepository.save(existente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        reservacionRepository.deleteById(id);
    }
}