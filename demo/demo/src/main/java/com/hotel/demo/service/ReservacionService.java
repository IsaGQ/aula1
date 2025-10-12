package com.hotel.demo.service;

import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.hotel.demo.model.habitacion;
import com.hotel.demo.model.reservacion;
import com.hotel.demo.repository.habitacionrespository;
import com.hotel.demo.repository.reservarepository;
import com.hotel.demo.dto.ReservacionDTO;
import com.hotel.demo.dto.ReservacionItemDTO;

@Service
public class ReservacionService {

    private final reservarepository reservaRepository;
    private final habitacionrespository habitacionRepository;

    public ReservacionService(reservarepository reservaRepository, habitacionrespository habitacionRepository) {
        this.reservaRepository = reservaRepository;
        this.habitacionRepository = habitacionRepository;
    }

    public List<reservacion> obtenerTodas() {
        return reservaRepository.findAll();
    }

    public reservacion obtenerPorId(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }

    @Transactional
    public reservacion crearReservaDesdeDTO(ReservacionDTO dto) {
        validarFechas(dto.getFechaLlegada(), dto.getFechaSalida());

        // contar por id según items (cada item tiene cantidad)
        Map<Long, Integer> contador = new HashMap<>();
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe incluir al menos una habitación en la reserva");
        }
        for (ReservacionItemDTO it : dto.getItems()) {
            if (it.getHabitacionId() == null || it.getCantidad() == null || it.getCantidad() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item inválido en la reserva");
            }
            contador.put(it.getHabitacionId(), contador.getOrDefault(it.getHabitacionId(), 0) + it.getCantidad());
        }

        // validar disponibilidad y cargar habitaciones
        Map<Long, habitacion> habitacionesMap = new HashMap<>();
        for (Map.Entry<Long, Integer> e : contador.entrySet()) {
            Long idHab = e.getKey();
            int unidades = e.getValue();
            habitacion habBD = habitacionRepository.findById(idHab)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada id: " + idHab));
            if (habBD.getCantidad() < unidades) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay suficientes unidades para id " + idHab);
            }
            habitacionesMap.put(idHab, habBD);
        }

        // descontar stock
        for (Map.Entry<Long, Integer> e : contador.entrySet()) {
            habitacion hab = habitacionesMap.get(e.getKey());
            hab.setCantidad(hab.getCantidad() - e.getValue());
            habitacionRepository.save(hab);
        }

        // crear entidad reservacion
        reservacion r = new reservacion();
        r.setNombreCompleto(dto.getNombreCompleto());
        r.setCedula(dto.getCedula());
        r.setCelular(dto.getCelular());
        r.setCorreo(dto.getCorreo());
        r.setFechaLlegada(dto.getFechaLlegada());
        r.setFechaSalida(dto.getFechaSalida());
        r.setConfirmada(false);

        // construir lista de habitaciones repetidas por cantidad (según tu modelo)
        List<habitacion> listaHabitaciones = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : contador.entrySet()) {
            habitacion hab = habitacionesMap.get(e.getKey());
            for (int i = 0; i < e.getValue(); i++) listaHabitaciones.add(hab);
        }
        r.setHabitaciones(listaHabitaciones);

        long dias = ChronoUnit.DAYS.between(dto.getFechaLlegada(), dto.getFechaSalida());
        if (dias <= 0) dias = 1;
        double total = 0.0;
        for (Map.Entry<Long, Integer> e : contador.entrySet()) {
            habitacion hab = habitacionesMap.get(e.getKey());
            total += hab.getPrecioPorNoche() * dias * e.getValue();
        }
        r.setPrecioTotal(total);

        return reservaRepository.save(r);
    }

    @Transactional
    public reservacion actualizarReserva(Long id, ReservacionDTO dto) {
        // lógica similar a la que ya tenías: obtener reserva, calcular diferencias, devolver/restar stock, recalcular total
        reservacion reservaExistente = reservaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        validarFechas(dto.getFechaLlegada(), dto.getFechaSalida());

        // Contar nuevo
        Map<Long, Integer> contadorNuevo = new HashMap<>();
        for (ReservacionItemDTO it : dto.getItems()) {
            contadorNuevo.put(it.getHabitacionId(), contadorNuevo.getOrDefault(it.getHabitacionId(), 0) + it.getCantidad());
        }

        // Contar existente (por id)
        Map<Long, Integer> contadorExistente = new HashMap<>();
        if (reservaExistente.getHabitaciones() != null) {
            for (habitacion h : reservaExistente.getHabitaciones()) {
                if (h != null && h.getId() != null) {
                    contadorExistente.put(h.getId(), contadorExistente.getOrDefault(h.getId(), 0) + 1);
                }
            }
        }

        // validar incrementos de stock
        for (Map.Entry<Long, Integer> e : contadorNuevo.entrySet()) {
            int nuevo = e.getValue();
            int exist = contadorExistente.getOrDefault(e.getKey(), 0);
            int diff = nuevo - exist;
            if (diff > 0) {
                habitacion habBD = habitacionRepository.findById(e.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada id: " + e.getKey()));
                if (habBD.getCantidad() < diff) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay suficientes unidades para id " + e.getKey());
                }
            }
        }

        // devolver stock de los que disminuyen
        for (Map.Entry<Long, Integer> e : contadorExistente.entrySet()) {
            int exist = e.getValue();
            int nuevo = contadorNuevo.getOrDefault(e.getKey(), 0);
            int diff = nuevo - exist;
            if (diff < 0) {
                habitacion habBD = habitacionRepository.findById(e.getKey()).orElse(null);
                if (habBD != null) {
                    habBD.setCantidad(habBD.getCantidad() + Math.abs(diff));
                    habitacionRepository.save(habBD);
                }
            }
        }

        // restar los incrementos
        for (Map.Entry<Long, Integer> e : contadorNuevo.entrySet()) {
            int nuevo = e.getValue();
            int exist = contadorExistente.getOrDefault(e.getKey(), 0);
            int diff = nuevo - exist;
            if (diff > 0) {
                habitacion habBD = habitacionRepository.findById(e.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada id: " + e.getKey()));
                habBD.setCantidad(habBD.getCantidad() - diff);
                habitacionRepository.save(habBD);
            }
        }

        // actualizar datos reserva
        reservaExistente.setNombreCompleto(dto.getNombreCompleto());
        reservaExistente.setCedula(dto.getCedula());
        reservaExistente.setCelular(dto.getCelular());
        reservaExistente.setCorreo(dto.getCorreo());
        reservaExistente.setFechaLlegada(dto.getFechaLlegada());
        reservaExistente.setFechaSalida(dto.getFechaSalida());

        // reconstruir lista de habitaciones
        List<habitacion> lista = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : contadorNuevo.entrySet()) {
            habitacion h = habitacionRepository.findById(e.getKey()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitación no encontrada id: " + e.getKey()));
            for (int i = 0; i < e.getValue(); i++) lista.add(h);
        }
        reservaExistente.setHabitaciones(lista);

        long dias = ChronoUnit.DAYS.between(reservaExistente.getFechaLlegada(), reservaExistente.getFechaSalida());
        if (dias <= 0) dias = 1;
        double total = 0.0;
        for (Map.Entry<Long, Integer> e : contadorNuevo.entrySet()) {
            habitacion habBD = habitacionRepository.findById(e.getKey()).orElse(null);
            if (habBD != null) total += habBD.getPrecioPorNoche() * dias * e.getValue();
        }
        reservaExistente.setPrecioTotal(total);

        return reservaRepository.save(reservaExistente);
    }

    @Transactional
    public void eliminarReserva(Long id) {
        reservacion reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) return;

        Map<Long, Integer> contador = new HashMap<>();
        if (reserva.getHabitaciones() != null) {
            for (habitacion h : reserva.getHabitaciones()) {
                if (h != null && h.getId() != null) {
                    contador.put(h.getId(), contador.getOrDefault(h.getId(), 0) + 1);
                }
            }
        }

        for (Map.Entry<Long, Integer> e : contador.entrySet()) {
            habitacion hab = habitacionRepository.findById(e.getKey()).orElse(null);
            if (hab != null) {
                hab.setCantidad(hab.getCantidad() + e.getValue());
                habitacionRepository.save(hab);
            }
        }

        reservaRepository.deleteById(id);
    }

    public boolean tieneReservasPorHabitacion(Long idHabitacion) {
        return reservaRepository.existsByHabitaciones_Id(idHabitacion);
    }

    // util
    private void validarFechas(LocalDate llegada, LocalDate salida) {
        if (llegada == null || salida == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fechas obligatorias");
        }
        if (!salida.isAfter(llegada)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha de salida debe ser posterior a llegada");
        }
    }
}
