package com.hotel.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Entity
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId; // id del usuario propietario del carrito

    private Date fechaLlegada;
    private Date fechaSalida;
    private double precioTotal;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaHabitacion> reservaHabitaciones = new ArrayList<>();

    private boolean confirmada;

    public Carrito() {}

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Date getFechaLlegada() { return fechaLlegada; }
    public void setFechaLlegada(LocalDate fechaLlegada2) { this.fechaLlegada = fechaLlegada2; }

    public Date getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida2) { this.fechaSalida = fechaSalida2; }

    public double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(double precioTotal) { this.precioTotal = precioTotal; }

    public List<ReservaHabitacion> getReservaHabitaciones() { return reservaHabitaciones; }
    public void setReservaHabitaciones(List<ReservaHabitacion> reservaHabitaciones) { this.reservaHabitaciones = reservaHabitaciones; }

    public boolean isConfirmada() { return confirmada; }
    public void setConfirmada(boolean confirmada) { this.confirmada = confirmada; }
}
