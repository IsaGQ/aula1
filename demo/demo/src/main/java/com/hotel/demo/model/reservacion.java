package com.hotel.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
<<<<<<< HEAD
@Table(name = "reservaciones")
public class reservacion {

=======
public class Reservacion {
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String nombreCompleto;
    private String correo;
    private String direccion;
    private String celular;
    private String documentoIdentidad;

    private LocalDate fechaLlegada;
    private LocalDate fechaSalida;

    private Double precioTotal;

<<<<<<< HEAD
    private String estado;
=======
    // Habitaciones reservadas (pueden ser varias)
    @ManyToMany
    private List<Habitacion> habitaciones;
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279

    // NUEVO: campo confirmada (para compatibilidad con repositorios y servicios)
    private Boolean confirmada;

    @OneToMany(mappedBy = "reservacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaHabitacion> reservaHabitaciones;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public reservacion() {}

    // Getters / Setters (añadir el get/set de confirmada)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }

    public LocalDate getFechaLlegada() { return fechaLlegada; }
    public void setFechaLlegada(LocalDate fechaLlegada) { this.fechaLlegada = fechaLlegada; }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(Date fechaSalida) { this.fechaSalida = fechaSalida; }

    public Double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(Double precioTotal) { this.precioTotal = precioTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Boolean getConfirmada() { return confirmada; }
    public void setConfirmada(Boolean confirmada) { this.confirmada = confirmada; }

    public List<ReservaHabitacion> getReservaHabitaciones() { return reservaHabitaciones; }
    public void setReservaHabitaciones(List<ReservaHabitacion> reservaHabitaciones) { this.reservaHabitaciones = reservaHabitaciones; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    public void prePersist() {
        if (this.estado == null) this.estado = "PENDIENTE";
        if (this.precioTotal == null) this.precioTotal = 0.0;
        if (this.confirmada == null) this.confirmada = false; // inicializa confirmada = false
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        if (this.confirmada == null) this.confirmada = false;
        this.updatedAt = LocalDateTime.now();
    }
<<<<<<< HEAD
=======

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public LocalDate getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(LocalDate fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<Habitacion> habitaciones) {
        this.habitaciones = habitaciones;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public void setConfirmada(boolean confirmada) {
        this.confirmada = confirmada;
    }

    
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
}
