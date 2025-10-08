package com.hotel.demo.service;

import com.hotel.demo.model.ReservaHabitacion;
import com.hotel.demo.model.habitacion;
import com.hotel.demo.model.reservacion;
import com.hotel.demo.model.Usuario;
import com.hotel.demo.repository.ReservaHabitacionRepository;
import com.hotel.demo.repository.habitacionrespository;
import com.hotel.demo.repository.reservarepository;
import com.hotel.demo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservacionService {

    private final reservarepository reservacionRepository;
    private final ReservaHabitacionRepository reservaHabitacionRepository;
    private final habitacionrespository habitacionRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservacionService(
            reservarepository reservacionRepository,
            ReservaHabitacionRepository reservaHabitacionRepository,
            habitacionrespository habitacionRepository,
            UsuarioRepository usuarioRepository) {
        this.reservacionRepository = reservacionRepository;
        this.reservaHabitacionRepository = reservaHabitacionRepository;
        this.habitacionRepository = habitacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /* ---------------------- Listados / consultas ---------------------- */

    public Page<reservacion> listarReservaciones(
            Integer page,
            Integer size,
            String estado,
            Long usuarioId,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String sortBy,
            String direction
    ) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size <= 0 ? 20 : size;
        String sortField = StringUtils.hasText(sortBy) ? sortBy : "createdAt";
        Sort.Direction dir = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(p, s, Sort.by(dir, sortField));

        Page<reservacion> pageResult = reservacionRepository.findAll(pageable);

        List<reservacion> filtered = pageResult.stream()
                .filter(r -> estado == null || estado.isBlank() || estado.equalsIgnoreCase(r.getEstado()))
                .filter(r -> usuarioId == null || (r.getUsuario() != null && usuarioId.equals(r.getUsuario().getId())))
                .filter(r -> {
                    if (fechaDesde == null && fechaHasta == null) return true;
                    if (r.getCreatedAt() == null) return false;
                    boolean afterDesde = fechaDesde == null || !r.getCreatedAt().toLocalDate().isBefore(fechaDesde);
                    boolean beforeHasta = fechaHasta == null || !r.getCreatedAt().toLocalDate().isAfter(fechaHasta);
                    return afterDesde && beforeHasta;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    public List<reservacion> listarTodas() {
        return reservacionRepository.findAll();
    }

    public List<reservacion> listarPorUsuario(Long usuarioId) {
        return reservacionRepository.findByUsuarioId(usuarioId);
    }

    public Optional<reservacion> obtenerPorId(Long id) {
        return reservacionRepository.findById(id);
    }

    /* ---------------------- Crear / Editar / Eliminar ---------------------- */

    @Transactional
    public reservacion crearReservacion(reservacion input) {
        if (input.getUsuario() != null && input.getUsuario().getId() != null) {
            Usuario u = usuarioRepository.findById(input.getUsuario().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado"));
            input.setUsuario(u);
        }

        reservacion saved = reservacionRepository.save(input);
        recalcularPrecioTotal(saved);
        return reservacionRepository.save(saved);
    }

    @Transactional
    public reservacion editarReservacion(Long id, reservacion datos) {
        reservacion exist = reservacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservación no encontrada"));

        exist.setFechaLlegada(datos.getFechaLlegada());
        exist.setFechaSalida(datos.getFechaSalida());
        exist.setNombreCompleto(datos.getNombreCompleto());
        exist.setCorreo(datos.getCorreo());
        exist.setDireccion(datos.getDireccion());
        exist.setCelular(datos.getCelular());
        exist.setDocumentoIdentidad(datos.getDocumentoIdentidad());
        exist.setEstado(datos.getEstado());

        if (datos.getUsuario() != null && datos.getUsuario().getId() != null) {
            Usuario u = usuarioRepository.findById(datos.getUsuario().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no encontrado"));
            exist.setUsuario(u);
        }

        recalcularPrecioTotal(exist);
        return reservacionRepository.save(exist);
    }

    @Transactional
    public void eliminarReservacion(Long id) {
        if (!reservacionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservación no encontrada");
        }
        reservacionRepository.deleteById(id);
    }

    /* ---------------------- Items de habitación ---------------------- */

    @Transactional
    public reservacion agregarItem(Long reservacionId, Long habitacionId, int cantidad) {
        reservacion r = reservacionRepository.findById(reservacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservación no encontrada"));
        habitacion h = habitacionRepository.findById(habitacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada"));

        ReservaHabitacion rh = new ReservaHabitacion();
        // ASUMIMOS que ReservaHabitacion tiene setHabitacion(habitacion)
        rh.setHabitacionId(h);
        rh.setCantidad(cantidad);

        long dias = 1;
        if (r.getFechaLlegada() != null && r.getFechaSalida() != null) {
            dias = ChronoUnit.DAYS.between(r.getFechaLlegada(), r.getFechaSalida());
            if (dias <= 0) dias = 1;
        }
        rh.setSubtotal(h.getPrecioPorNoche() * cantidad * dias);
        rh.setReservacion(r);

        reservaHabitacionRepository.save(rh);
        if (r.getReservaHabitaciones() == null) r.setReservaHabitaciones(new java.util.ArrayList<>());
        r.getReservaHabitaciones().add(rh);

        recalcularPrecioTotal(r);
        return reservacionRepository.save(r);
    }

    @Transactional
    public reservacion actualizarItem(Long reservaHabitacionId, int nuevaCantidad) {
        ReservaHabitacion rh = reservaHabitacionRepository.findById(reservaHabitacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado"));

        // aquí obtenemos la habitación asociada correctamente
        final habitacion h = (rh.getHabitacionId() != null)
                ? habitacionRepository.findById(rh.getHabitacionId().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada"))
                : null;

        if (h == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación asociada al item no existe");
        }

        if (nuevaCantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad inválida");
        }

        long dias = 1;
        reservacion r = rh.getReservacion();
        if (r.getFechaLlegada() != null && r.getFechaSalida() != null) {
            dias = ChronoUnit.DAYS.between(r.getFechaLlegada(), r.getFechaSalida());
            if (dias <= 0) dias = 1;
        }

        rh.setCantidad(nuevaCantidad);
        rh.setSubtotal(h.getPrecioPorNoche() * nuevaCantidad * dias);
        reservaHabitacionRepository.save(rh);

        recalcularPrecioTotal(r);
        return reservacionRepository.save(r);
    }

    @Transactional
    public reservacion eliminarItem(Long reservaHabitacionId) {
        ReservaHabitacion rh = reservaHabitacionRepository.findById(reservaHabitacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado"));
        reservacion r = rh.getReservacion();

        r.getReservaHabitaciones().removeIf(i -> i.getId().equals(rh.getId()));
        reservaHabitacionRepository.delete(rh);

        recalcularPrecioTotal(r);
        return reservacionRepository.save(r);
    }

    /* ---------------------- Estados y totales ---------------------- */

    @Transactional
    public reservacion cambiarEstado(Long reservacionId, String nuevoEstado) {
        reservacion r = reservacionRepository.findById(reservacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservación no encontrada"));

        r.setEstado(nuevoEstado);
        return reservacionRepository.save(r);
    }

    @Transactional
    public void recalcularPrecioTotal(reservacion r) {
        if (r == null) return;

        long dias = 1;
        if (r.getFechaLlegada() != null && r.getFechaSalida() != null) {
            dias = ChronoUnit.DAYS.between(r.getFechaLlegada(), r.getFechaSalida());
            if (dias <= 0) dias = 1;
        }

        double total = 0.0;

        if (r.getReservaHabitaciones() != null) {
            for (ReservaHabitacion rh : r.getReservaHabitaciones()) {
                habitacion h = (rh.getHabitacionId() != null)
                        ? habitacionRepository.findById(rh.getHabitacionId().getId()).orElse(null)
                        : null;
                double precioUnit = (h != null) ? h.getPrecioPorNoche() : 0.0;
                double subtotal = precioUnit * rh.getCantidad() * dias;
                rh.setSubtotal(subtotal);
                reservaHabitacionRepository.save(rh);
                total += subtotal;
            }
        }

        r.setPrecioTotal(total);
    }
}
