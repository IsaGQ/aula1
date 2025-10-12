package com.hotel.demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "reserva_habitacion")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReservaHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // referencia a la habitacion (muchos items pueden referenciar la misma habitacion)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitacion_id")
    private habitacion habitacion;

    // cantidad solicitada de esta habitacion
    private Integer cantidad;

    // subtotal calculado (precioPorNoche * cantidad * dias) — se recalcula en servicio
    private Double subtotal = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(habitacion habitacion) { this.habitacion = habitacion; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Carrito getCarrito() { return carrito; }
    public void setCarrito(Carrito carrito) { this.carrito = carrito; }
}
