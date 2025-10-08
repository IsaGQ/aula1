package com.hotel.demo.controller;

import java.util.List;
import java.util.Optional;

<<<<<<< HEAD
import org.springframework.web.bind.annotation.*;
import com.hotel.demo.model.reservacion;
import com.hotel.demo.service.ReservacionService;
=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.demo.model.Reservacion;
import com.hotel.demo.service.ReservaService;
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

<<<<<<< HEAD
    private final ReservacionService reservaService;

    // Inyección por constructor
    public reservacontroller(ReservacionService reservaService) {
        this.reservaService = reservaService;
    }
=======
    @Autowired
    private ReservaService reservaService;
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279

    @GetMapping
    public List<Reservacion> listarReservas() {
        return reservaService.obtenerTodas();
    }

    @GetMapping("/{id}")
<<<<<<< HEAD
    public Optional<reservacion> obtenerReserva(@PathVariable Long id) {
=======
    public Reservacion obtenerReserva(@PathVariable Long id) {
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
        return reservaService.obtenerPorId(id);
    }

    /**
     * Crear reserva.
     * El service valida fechas y stock y descuenta cantidades.
     * Se espera body con 'fechaLlegada', 'fechaSalida' y 'habitaciones' (lista de habitaciones;
     * según tu modelo actual, repetir la misma habitacion N veces para reservar N unidades).
     */
    @PostMapping
<<<<<<< HEAD
    public reservacion crearReserva(@RequestBody reservacion reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("Cuerpo de la reserva vacío.");
        }
        if (reserva.getReservaHabitaciones() == null || reserva.getReservaHabitaciones().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos una habitación en la reserva.");
        }
        // El servicio ya hace validaciones adicionales (fechas, stock, cálculo de precio y descuento de inventario)
        return reservaService.crearReservacion(reserva);
    }

    @PutMapping("/{id}")
    public reservacion actualizarReserva(@PathVariable Long id, @RequestBody reservacion reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("Cuerpo de la reserva vacío.");
        }
=======
    public Reservacion crearReserva(@RequestBody Reservacion reserva) {
        return reservaService.crearReserva(reserva);
    }

    @PutMapping("/{id}")
    public Reservacion actualizarReserva(@PathVariable Long id, @RequestBody Reservacion reserva) {
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
        return reservaService.actualizarReserva(id, reserva);
    }

    @DeleteMapping("/{id}")
    public void eliminarReserva(@PathVariable Long id) {
        reservaService.eliminarReservacion(id);
    }

    @GetMapping("/habitacion/{id}/tiene-reservas")
    public boolean verificarReservasPorHabitacion(@PathVariable Long id) {
        return reservaService.tieneReservasPorHabitacion(id);
    }
}
