package com.hotel.demo.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.hotel.demo.model.reservacion;
import com.hotel.demo.service.ReservacionService;
import com.hotel.demo.dto.ReservacionDTO;

@RestController
@RequestMapping("/api/reservas")
public class reservacontroller {

    private final ReservacionService reservaService;

    public reservacontroller(ReservacionService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<reservacion> listarReservas() {
        return reservaService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<reservacion> obtenerReserva(@PathVariable Long id) {
        reservacion r = reservaService.obtenerPorId(id);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(r);
    }

    @PostMapping
    public ResponseEntity<reservacion> crearReserva(@RequestBody ReservacionDTO dto) {
        reservacion creada = reservaService.crearReservaDesdeDTO(dto);
        return ResponseEntity.status(201).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<reservacion> actualizarReserva(@PathVariable Long id, @RequestBody ReservacionDTO dto) {
        reservacion actualizada = reservaService.actualizarReserva(id, dto);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/habitacion/{id}/tiene-reservas")
    public boolean verificarReservasPorHabitacion(@PathVariable Long id) {
        return reservaService.tieneReservasPorHabitacion(id);
    }
}
