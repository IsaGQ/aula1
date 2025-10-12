package com.hotel.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.demo.model.habitacion;
import com.hotel.demo.repository.habitacionrespository;

@Service
public class habitacionservice {

    @Autowired
    private habitacionrespository habitacionRepository;


    public List<habitacion> obtenerTodasLasHabitaciones() {
        return habitacionRepository.findAll();
    }

    public habitacion obtenerHabitacionPorId(Long habitacionId) {
        return habitacionRepository.findById(habitacionId).orElse(null);
    }

    public habitacion crearHabitacion(habitacion habitacion) {
        if (habitacion.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        return habitacionRepository.save(habitacion);
    }

    public habitacion actualizarHabitacion(Long id, habitacion habitacionActualizada) {
        habitacion habitacionExistente = habitacionRepository.findById(id).orElse(null);
        if (habitacionExistente == null) {
            return null;
        }

        habitacionExistente.setTipo(habitacionActualizada.getTipo());
        habitacionExistente.setDescripcion(habitacionActualizada.getDescripcion());
        habitacionExistente.setPrecioPorNoche(habitacionActualizada.getPrecioPorNoche());
        habitacionExistente.setCapacidad(habitacionActualizada.getCapacidad());
        habitacionExistente.setCantidad(habitacionActualizada.getCantidad());
        habitacionExistente.setImagenUrl(habitacionActualizada.getImagenUrl());

        // Validación y actualización de cantidad
        if (habitacionActualizada.getCantidad() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        habitacionExistente.setCantidad(habitacionActualizada.getCantidad());

        habitacion habitacionGuardada = habitacionRepository.save(habitacionExistente);

        // ACTUALIZAR LAS RESERVAS QUE TIENEN ESTA HABITACIÓN (solo recalcula precio si cambia precio/fechas)
        

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

}

