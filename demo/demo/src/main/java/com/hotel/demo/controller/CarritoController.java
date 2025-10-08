package com.hotel.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.hotel.demo.model.Carrito;
import com.hotel.demo.service.CarritoService;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/{usuarioId}")
    public Carrito obtenerCarrito(@PathVariable Long usuarioId) {
        return carritoService.obtenerCarrito(usuarioId);
    }

    @PostMapping("/{usuarioId}/agregar")
    public Carrito agregarHabitacion(
            @PathVariable Long usuarioId,
            @RequestParam Long habitacionId,
            @RequestParam int cantidad) {
        return carritoService.agregarHabitacion(usuarioId, habitacionId, cantidad);
    }

    @PutMapping("/{usuarioId}/item/{reservaId}")
    public Carrito actualizarCantidad(
            @PathVariable Long usuarioId,
            @PathVariable Long reservaId,
            @RequestParam int cantidad) {
        return carritoService.actualizarCantidad(usuarioId, reservaId, cantidad);
    }

    @DeleteMapping("/{usuarioId}/item/{reservaId}")
    public Carrito eliminarItem(
            @PathVariable Long usuarioId,
            @PathVariable Long reservaId) {
        return carritoService.eliminarItem(usuarioId, reservaId);
    }

    @PutMapping("/{usuarioId}/confirmar")
    public Carrito confirmarCarrito(@PathVariable Long usuarioId) {
        return carritoService.confirmarCarrito(usuarioId);
    }
}
