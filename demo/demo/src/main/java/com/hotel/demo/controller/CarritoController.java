package com.hotel.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.hotel.demo.dto.*;
import com.hotel.demo.service.CarritoService;
import com.hotel.demo.model.reservacion;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CarritoDTO> obtenerCarrito(@PathVariable String userId) {
        CarritoDTO dto = carritoService.obtenerCarrito(userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CarritoDTO> agregarAlCarrito(@PathVariable String userId, @RequestBody AddToCartRequest req) {
        CarritoDTO dto = carritoService.agregarAlCarrito(userId, req);
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/{userId}/items/{reservaItemId}")
    public ResponseEntity<CarritoDTO> actualizarItem(@PathVariable String userId, @PathVariable Long reservaItemId, @RequestBody UpdateItemRequest req) {
        CarritoDTO dto = carritoService.actualizarItem(userId, reservaItemId, req);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{userId}/items/{reservaItemId}")
    public ResponseEntity<CarritoDTO> eliminarItem(@PathVariable String userId, @PathVariable Long reservaItemId) {
        CarritoDTO dto = carritoService.eliminarItem(userId, reservaItemId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Confirmar carrito: recibimos datos del cliente mínimo (nombre, cedula, celular, correo).
     * Frontend puede pedir un modal con formulario para estos datos antes de llamar este endpoint.
     */
    @PostMapping("/{userId}/confirm")
    public ResponseEntity<reservacion> confirmarCarrito(
            @PathVariable String userId,
            @RequestParam String nombreCompleto,
            @RequestParam String cedula,
            @RequestParam String celular,
            @RequestParam String correo) {

        reservacion creada = carritoService.confirmarCarrito(userId, nombreCompleto, cedula, celular, correo);
        return ResponseEntity.ok(creada);
    }
}
