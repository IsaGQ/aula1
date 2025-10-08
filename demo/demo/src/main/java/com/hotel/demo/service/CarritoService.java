package com.hotel.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;

import com.hotel.demo.model.Carrito;
import com.hotel.demo.model.ReservaHabitacion;
import com.hotel.demo.model.habitacion;
import com.hotel.demo.model.reservacion;
import com.hotel.demo.model.Usuario;

import com.hotel.demo.repository.CarritoRepository;
import com.hotel.demo.repository.ReservaHabitacionRepository;
import com.hotel.demo.repository.habitacionrespository;
import com.hotel.demo.repository.reservarepository;
import com.hotel.demo.repository.UsuarioRepository;

import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CarritoService consistente con los modelos:
 * - Habitacion
 * - ReservaHabitacion (tiene getHabitacion(), getCantidad(), getSubtotal(), getCarrito(), getId())
 * - Carrito (tiene getFechaLlegada(), getFechaSalida(), getPrecioTotal(), getReservaHabitaciones())
 * - Reservacion
 *
 * Si tus modelos usan nombres distintos, pégamelos y adapto el servicio.
 */
@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ReservaHabitacionRepository reservaHabitacionRepository;

    @Autowired
    private habitacionrespository habitacionRepository;

    @Autowired
    private reservarepository reservacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener o crear carrito del usuario
    public Carrito obtenerCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuarioId(usuarioId);
                    nuevo.setPrecioTotal(0.0);
                    return carritoRepository.save(nuevo);
                });
    }

    // Agregar al carrito
    @Transactional
    public Carrito agregarAlCarrito(Long usuarioId, Long habitacionId, Integer cantidad, LocalDate fechaLlegada, LocalDate fechaSalida) {
        if (cantidad == null || cantidad <= 0) throw new IllegalArgumentException("Cantidad inválida");

        Habitacion h = habitacionRepository.findById(habitacionId)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + habitacionId));

        Carrito carrito = obtenerCarrito(usuarioId);

        // Si el cliente envió fechas, actualizarlas en el carrito
        if (fechaLlegada != null) carrito.setFechaLlegada(fechaLlegada);
        if (fechaSalida != null) carrito.setFechaSalida(fechaSalida);

        long dias = 1;
        if (carrito.getFechaLlegada() != null && carrito.getFechaSalida() != null) {
            dias = ChronoUnit.DAYS.between(carrito.getFechaLlegada(), carrito.getFechaSalida());
            if (dias <= 0) dias = 1;
        }

        double precioUnit = h.getPrecioPorNoche() == null ? 0.0 : h.getPrecioPorNoche();
        double subtotal = precioUnit * cantidad * dias;

        ReservaHabitacion item = new ReservaHabitacion();
        item.setHabitacion(h);
        item.setCantidad(cantidad);
        item.setSubtotal(subtotal);
        item.setCarrito(carrito);

        // persistir item (necesario para que tenga id si lo vas a usar después)
        reservaHabitacionRepository.save(item);

        if (carrito.getReservaHabitaciones() == null) carrito.setReservaHabitaciones(new ArrayList<>());
        carrito.getReservaHabitaciones().add(item);

        carrito.setPrecioTotal((carrito.getPrecioTotal() == null ? 0.0 : carrito.getPrecioTotal()) + subtotal);
        return carritoRepository.save(carrito);
    }

    // Actualizar cantidad item del carrito
    @Transactional
    public Carrito actualizarItem(Long usuarioId, Long reservaHabitacionId, Integer nuevaCantidad) {
        if (nuevaCantidad == null || nuevaCantidad <= 0) throw new IllegalArgumentException("Cantidad inválida");

        Carrito carrito = obtenerCarrito(usuarioId);

        ReservaHabitacion item = reservaHabitacionRepository.findById(reservaHabitacionId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        // verificar pertenencia
        if (item.getCarrito() == null || !item.getCarrito().getId().equals(carrito.getId())) {
            throw new RuntimeException("El item no pertenece al carrito del usuario");
        }

        Habitacion h = item.getHabitacion();
        if (h == null) throw new RuntimeException("Habitación asociada al item no encontrada");

        long dias = 1;
        if (carrito.getFechaLlegada() != null && carrito.getFechaSalida() != null) {
            dias = ChronoUnit.DAYS.between(carrito.getFechaLlegada(), carrito.getFechaSalida());
            if (dias <= 0) dias = 1;
        }

        double precioUnit = h.getPrecioPorNoche() == null ? 0.0 : h.getPrecioPorNoche();
        double oldSubtotal = item.getSubtotal() == null ? 0.0 : item.getSubtotal();
        double newSubtotal = precioUnit * nuevaCantidad * dias;

        item.setCantidad(nuevaCantidad);
        item.setSubtotal(newSubtotal);
        reservaHabitacionRepository.save(item);

        carrito.setPrecioTotal((carrito.getPrecioTotal() == null ? 0.0 : carrito.getPrecioTotal()) - oldSubtotal + newSubtotal);
        return carritoRepository.save(carrito);
    }

    // Eliminar item
    @Transactional
    public Carrito eliminarItem(Long usuarioId, Long reservaHabitacionId) {
        Carrito carrito = obtenerCarrito(usuarioId);

        ReservaHabitacion item = reservaHabitacionRepository.findById(reservaHabitacionId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (item.getCarrito() == null || !item.getCarrito().getId().equals(carrito.getId())) {
            throw new RuntimeException("El item no pertenece al carrito del usuario");
        }

        double sub = item.getSubtotal() == null ? 0.0 : item.getSubtotal();

        carrito.getReservaHabitaciones().removeIf(i -> i.getId().equals(item.getId()));
        reservaHabitacionRepository.delete(item);

        carrito.setPrecioTotal((carrito.getPrecioTotal() == null ? 0.0 : carrito.getPrecioTotal()) - sub);
        return carritoRepository.save(carrito);
    }

    // Confirmar carrito (solo marcar)
    @Transactional
    public Carrito marcarConfirmada(Long usuarioId) {
        Carrito carrito = obtenerCarrito(usuarioId);
        carrito.setConfirmada(true);
        return carritoRepository.save(carrito);
    }

    // Confirmar carrito y crear reservación (transaccional)
    @Transactional
    public Reservacion confirmarCarritoYCrearReservacion(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para usuario " + usuarioId));

        if (carrito.getReservaHabitaciones() == null || carrito.getReservaHabitaciones().isEmpty()) {
            throw new RuntimeException("Carrito vacío");
        }

        // Validar stock y descontar
        for (ReservaHabitacion item : new ArrayList<>(carrito.getReservaHabitaciones())) {
            Habitacion h = item.getHabitacion();
            if (h == null || h.getId() == null) {
                throw new RuntimeException("Habitación inválida en item id=" + item.getId());
            }

            Habitacion actual = habitacionRepository.findById(h.getId())
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada: " + h.getId()));

            int stock = actual.getCantidad() == null ? 0 : actual.getCantidad();
            int cantidadSolicitada = item.getCantidad() == null ? 0 : item.getCantidad();

            if (stock < cantidadSolicitada) {
                throw new RuntimeException("Stock insuficiente para habitación id=" + actual.getId());
            }

            actual.setCantidad(stock - cantidadSolicitada);
            habitacionRepository.save(actual);
        }

        // Crear reservación
        Reservacion reservacion = new Reservacion();
        Optional<Usuario> optUser = usuarioRepository.findById(usuarioId);
        if (optUser.isPresent()) {
            Usuario u = optUser.get();
            reservacion.setUsuario(u);
            reservacion.setNombreCompleto(u.getNombre());
            reservacion.setCorreo(u.getCorreo());
            reservacion.setCelular(u.getCelular());
            reservacion.setDireccion(u.getDireccion());
            reservacion.setDocumentoIdentidad(u.getDocumentoIdentidad());
        }

        reservacion.setFechaLlegada(carrito.getFechaLlegada());
        reservacion.setFechaSalida(carrito.getFechaSalida());
        reservacion.setPrecioTotal(carrito.getPrecioTotal() == null ? 0.0 : carrito.getPrecioTotal());
        reservacion.setEstado("CONFIRMADA");

        // Mapear items
        List<ReservaHabitacion> itemsReservacion = new ArrayList<>();
        for (ReservaHabitacion itemCarrito : new ArrayList<>(carrito.getReservaHabitaciones())) {
            ReservaHabitacion rh = new ReservaHabitacion();
            rh.setHabitacion(itemCarrito.getHabitacion());
            rh.setCantidad(itemCarrito.getCantidad());
            rh.setSubtotal(itemCarrito.getSubtotal());
            rh.setReservacion(reservacion);
            itemsReservacion.add(rh);
        }
        reservacion.setReservaHabitaciones(itemsReservacion);

        // Guardar reservación
        Reservacion saved = reservacionRepository.save(reservacion);

        // Vaciar carrito y eliminar items
        for (ReservaHabitacion it : new ArrayList<>(carrito.getReservaHabitaciones())) {
            reservaHabitacionRepository.delete(it);
        }
        carrito.getReservaHabitaciones().clear();
        carrito.setPrecioTotal(0.0);
        carrito.setConfirmada(true);
        carritoRepository.save(carrito);

        return saved;
    }
}
