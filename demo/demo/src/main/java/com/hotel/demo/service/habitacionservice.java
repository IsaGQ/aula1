package com.hotel.demo.service;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
import com.hotel.demo.model.ReservaHabitacion;
import com.hotel.demo.model.habitacion;
import com.hotel.demo.model.reservacion;
import com.hotel.demo.repository.habitacionrespository;
import com.hotel.demo.repository.reservarepository;
=======
import com.hotel.demo.model.Habitacion;
import com.hotel.demo.model.Reservacion;
import com.hotel.demo.repository.HabitacionRespository;
import com.hotel.demo.repository.ReservaRepository;
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279

@Service
public class HabitacionService {

    @Autowired
    private HabitacionRespository habitacionRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Habitacion> obtenerTodasLasHabitaciones() {
        return habitacionRepository.findAll();
    }

<<<<<<< HEAD
    public habitacion obtenerHabitacionPorId(Long habitacionId) {
        return habitacionRepository.findById(habitacionId).orElse(null);
    }

    public habitacion crearHabitacion(habitacion habitacion) {
        if (habitacion.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
=======
    public Habitacion obtenerHabitacionPorId(Long id) {
        return habitacionRepository.findById(id).orElse(null);
    }

    public Habitacion crearHabitacion(Habitacion habitacion) {
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
        return habitacionRepository.save(habitacion);
    }

    public Habitacion actualizarHabitacion(Long id, Habitacion habitacionActualizada) {
        Habitacion habitacionExistente = habitacionRepository.findById(id).orElse(null);
        if (habitacionExistente == null) {
            return null;
        }

        habitacionExistente.setTipo(habitacionActualizada.getTipo());
        habitacionExistente.setDescripcion(habitacionActualizada.getDescripcion());
        habitacionExistente.setPrecioPorNoche(habitacionActualizada.getPrecioPorNoche());
        habitacionExistente.setCapacidad(habitacionActualizada.getCapacidad());
        habitacionExistente.setCantidad(habitacionActualizada.getCantidad());
        habitacionExistente.setImagenUrl(habitacionActualizada.getImagenUrl());

<<<<<<< HEAD
        // Validación y actualización de cantidad
        if (habitacionActualizada.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        habitacionExistente.setCantidad(habitacionActualizada.getCantidad());

        habitacion habitacionGuardada = habitacionRepository.save(habitacionExistente);

        // ACTUALIZAR LAS RESERVAS QUE TIENEN ESTA HABITACIÓN (solo recalcula precio si cambia precio/fechas)
        List<reservacion> reservas = reservaRepository.findAll();

        for (reservacion reserva : reservas) {
            boolean contiene = reserva.getReservaHabitaciones().stream()
=======
        Habitacion habitacionGuardada = habitacionRepository.save(habitacionExistente);

        //ACTUALIZAR LAS RESERVAS QUE TIENEN ESTA HABITACIÓN
        List<Reservacion> reservas = reservaRepository.findAll();

        for (Reservacion reserva : reservas) {
            boolean contiene = reserva.getHabitaciones().stream()
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
                .anyMatch(h -> h.getId().equals(id));

            if (contiene) {
                // Recalcular precio total
                long dias = ChronoUnit.DAYS.between(reserva.getFechaLlegada(), reserva.getFechaSalida());

                double nuevoTotal = 0.0;
<<<<<<< HEAD
                for (ReservaHabitacion h : reserva.getReservaHabitaciones()) {
                    habitacion actual = habitacionRepository.findById(h.getId()).orElse(null);
=======
                for (Habitacion h : reserva.getHabitaciones()) {
                    Habitacion actual = habitacionRepository.findById(h.getId()).orElse(null);
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
                    if (actual != null) {
                        nuevoTotal += actual.getPrecioPorNoche() * dias;
                    }
                }

                reserva.setPrecioTotal(nuevoTotal);
                reservaRepository.save(reserva);
            }
        }

        return habitacionGuardada;
    }

    public void eliminarHabitacion(Long id) {
        if (!habitacionRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe la habitación con id: " + id);
        }
        habitacionRepository.deleteById(id);
    }

    /**
     * Reserva (descuenta) una cantidad de habitaciones.
     * Método transaccional para mantener consistencia.
     */
    @Transactional
    public habitacion reservarHabitacion(Long id, int cantidadReservada) {
        if (cantidadReservada <= 0) {
            throw new IllegalArgumentException("La cantidad a reservar debe ser mayor a 0.");
        }

        habitacion hab = habitacionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada con id: " + id));

        int disponible = hab.getCantidad();
        if (disponible < cantidadReservada) {
            throw new IllegalStateException("No hay suficiente disponibilidad. Disponibles: " + disponible);
        }

        hab.setCantidad(disponible - cantidadReservada);
        return habitacionRepository.save(hab);
    }

    /**
     * Método utilitario para aumentar la cantidad (por ejemplo, al cancelar una reserva).
     */
    @Transactional
    public habitacion devolverCantidad(Long id, int cantidadDevolver) {
        if (cantidadDevolver <= 0) {
            throw new IllegalArgumentException("La cantidad a devolver debe ser mayor a 0.");
        }

        habitacion hab = habitacionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada con id: " + id));

        hab.setCantidad(hab.getCantidad() + cantidadDevolver);
        return habitacionRepository.save(hab);
    }

    public habitacion obtenerHabitacionPorId(habitacion habitacionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerHabitacionPorId'");
    }
}

