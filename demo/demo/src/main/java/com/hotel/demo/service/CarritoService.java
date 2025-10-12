package com.hotel.demo.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.hotel.demo.model.*;
import com.hotel.demo.dto.*;
import com.hotel.demo.repository.*;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ReservaHabitacionRepository reservaHabitacionRepository;
    private final habitacionrespository habitacionRepository;
    private final ReservacionService reservacionService;

    public CarritoService(CarritoRepository carritoRepository,
                          ReservaHabitacionRepository reservaHabitacionRepository,
                          habitacionrespository habitacionRepository,
                          ReservacionService reservacionService) {
        this.carritoRepository = carritoRepository;
        this.reservaHabitacionRepository = reservaHabitacionRepository;
        this.habitacionRepository = habitacionRepository;
        this.reservacionService = reservacionService;
    }

    public CarritoDTO obtenerCarrito(String userId) {
        Carrito carrito = carritoRepository.findByUserId(userId).orElseGet(() -> {
            Carrito c = new Carrito();
            c.setUserId(userId);
            c.setPrecioTotal(0.0);
            return carritoRepository.save(c);
        });
        return toDTO(carrito);
    }

    @Transactional
    public CarritoDTO agregarAlCarrito(String userId, AddToCartRequest req) {
        if (req.getCantidad() == null || req.getCantidad() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad inválida");
        }
        LocalDate llegada = parseDate(req.getFechaLlegada());
        LocalDate salida = parseDate(req.getFechaSalida());
        if (llegada == null || salida == null || !salida.isAfter(llegada)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fechas inválidas");
        }

        Carrito carrito = carritoRepository.findByUserId(userId).orElseGet(() -> {
            Carrito c = new Carrito();
            c.setUserId(userId);
            c.setFechaLlegada(llegada);
            c.setFechaSalida(salida);
            return carritoRepository.save(c);
        });

        // si el carrito ya tiene fechas y vienen otras, actualizarlas
        carrito.setFechaLlegada(llegada);
        carrito.setFechaSalida(salida);

        habitacion hab = habitacionRepository.findById(req.getHabitacionId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada"));

        // No descontamos stock al agregar al carrito; se descuenta al confirmar (o puedes acomodarlo)
        // Si prefieres reservar stock cuando se agrega al carrito, implementa bloqueo temporal.

        // Buscar si ya existe item para esa habitacion
        Optional<ReservaHabitacion> existente = carrito.getReservaHabitaciones().stream()
            .filter(i -> i.getHabitacion().getId().equals(hab.getId()))
            .findFirst();

        long dias = ChronoUnit.DAYS.between(llegada, salida);
        if (dias <= 0) dias = 1;
        double subtotal = hab.getPrecioPorNoche() * dias * req.getCantidad();

        if (existente.isPresent()) {
            ReservaHabitacion item = existente.get();
            item.setCantidad(item.getCantidad() + req.getCantidad());
            item.setSubtotal(item.getSubtotal() + subtotal);
            reservaHabitacionRepository.save(item);
        } else {
            ReservaHabitacion item = new ReservaHabitacion();
            item.setHabitacion(hab);
            item.setCantidad(req.getCantidad());
            item.setSubtotal(subtotal);
            carrito.addItem(item);
            reservaHabitacionRepository.save(item);
        }

        // recalcular total
        recalcularTotal(carrito);
        carritoRepository.save(carrito);

        return toDTO(carrito);
    }

    @Transactional
    public CarritoDTO actualizarItem(String userId, Long reservaItemId, UpdateItemRequest req) {
        Carrito carrito = carritoRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado"));
        ReservaHabitacion item = carrito.getReservaHabitaciones().stream()
            .filter(i -> i.getId().equals(reservaItemId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado en carrito"));

        if (req.getCantidad() == null || req.getCantidad() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad inválida");
        }

        LocalDate llegada = carrito.getFechaLlegada();
        LocalDate salida = carrito.getFechaSalida();
        long dias = (llegada != null && salida != null) ? ChronoUnit.DAYS.between(llegada, salida) : 1;
        if (dias <= 0) dias = 1;

        item.setCantidad(req.getCantidad());
        item.setSubtotal(item.getHabitacion().getPrecioPorNoche() * req.getCantidad() * dias);
        reservaHabitacionRepository.save(item);

        recalcularTotal(carrito);
        carritoRepository.save(carrito);

        return toDTO(carrito);
    }

    @Transactional
    public CarritoDTO eliminarItem(String userId, Long reservaItemId) {
        Carrito carrito = carritoRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado"));
        ReservaHabitacion item = carrito.getReservaHabitaciones().stream()
            .filter(i -> i.getId().equals(reservaItemId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado"));

        carrito.removeItem(item);
        reservaHabitacionRepository.delete(item);

        recalcularTotal(carrito);
        carritoRepository.save(carrito);

        return toDTO(carrito);
    }

    /**
     * Confirmar carrito: valida disponibilidad en stock, descuenta stock, crea una reservacion
     * usando ReservacionService (con DTO) y borra el carrito (o lo vacía).
     */
    @Transactional
    public reservacion confirmarCarrito(String userId, String nombreCompleto, String cedula, String celular, String correo) {
        Carrito carrito = carritoRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado"));

        if (carrito.getReservaHabitaciones() == null || carrito.getReservaHabitaciones().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
        }

        LocalDate llegada = carrito.getFechaLlegada();
        LocalDate salida = carrito.getFechaSalida();
        if (llegada == null || salida == null || !salida.isAfter(llegada)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fechas inválidas en carrito");
        }

        // Contar por habitacionId
        Map<Long, Integer> contador = new HashMap<>();
        for (ReservaHabitacion it : carrito.getReservaHabitaciones()) {
            contador.put(it.getHabitacion().getId(), contador.getOrDefault(it.getHabitacion().getId(), 0) + it.getCantidad());
        }

        // Validar disponibilidad
        for (Map.Entry<Long, Integer> e : contador.entrySet()) {
            habitacion hab = habitacionRepository.findById(e.getKey())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada id: " + e.getKey()));
            if (hab.getCantidad() < e.getValue()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay suficientes unidades para la habitación id " + e.getKey());
            }
        }

        // Preparar ReservacionDTO y items
        com.hotel.demo.dto.ReservacionDTO reservDto = new com.hotel.demo.dto.ReservacionDTO();
        reservDto.setNombreCompleto(nombreCompleto);
        reservDto.setCedula(cedula);
        reservDto.setCelular(celular);
        reservDto.setCorreo(correo);
        reservDto.setFechaLlegada(llegada);
        reservDto.setFechaSalida(salida);

        List<com.hotel.demo.dto.ReservacionItemDTO> items = contador.entrySet().stream().map(e -> {
            com.hotel.demo.dto.ReservacionItemDTO it = new com.hotel.demo.dto.ReservacionItemDTO();
            it.setHabitacionId(e.getKey());
            it.setCantidad(e.getValue());
            return it;
        }).collect(Collectors.toList());
        reservDto.setItems(items);

        // Llamar al servicio de reservas (descuenta stock dentro de él)
        reservacion creada = reservacionService.crearReservaDesdeDTO(reservDto);

        // Vaciar carrito (o eliminarlo por completo si prefieres)
        carrito.getReservaHabitaciones().clear();
        carrito.setPrecioTotal(0.0);
        carritoRepository.save(carrito);

        return creada;
    }

    // ----- helpers -----
    private void recalcularTotal(Carrito carrito) {
        double total = carrito.getReservaHabitaciones().stream()
            .mapToDouble(i -> i.getSubtotal() == null ? 0.0 : i.getSubtotal())
            .sum();
        carrito.setPrecioTotal(total);
    }

    private LocalDate parseDate(String s) {
        if (s == null) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private CarritoDTO toDTO(Carrito carrito) {
        CarritoDTO dto = new CarritoDTO();
        dto.setId(carrito.getId());
        dto.setUserId(carrito.getUserId());
        dto.setFechaLlegada(carrito.getFechaLlegada() != null ? carrito.getFechaLlegada().toString() : null);
        dto.setFechaSalida(carrito.getFechaSalida() != null ? carrito.getFechaSalida().toString() : null);
        dto.setPrecioTotal(carrito.getPrecioTotal());
        List<ReservaHabitacionDTO> items = carrito.getReservaHabitaciones().stream().map(i -> {
            ReservaHabitacionDTO r = new ReservaHabitacionDTO();
            r.setId(i.getId());
            r.setHabitacionId(i.getHabitacion().getId());
            r.setTipo(i.getHabitacion().getTipo());
            r.setPrecioPorNoche(i.getHabitacion().getPrecioPorNoche());
            r.setCantidad(i.getCantidad());
            r.setSubtotal(i.getSubtotal());
            r.setImagenUrl(i.getHabitacion().getImagenUrl());
            return r;
        }).collect(Collectors.toList());
        dto.setReservaHabitaciones(items);
        return dto;
    }
}
