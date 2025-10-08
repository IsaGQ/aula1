/*package com.hotel.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reserva_habitacion")
public class ReservaHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservacion_id")
    private reservacion reservacion;

    @ManyToOne
    @JoinColumn(name = "habitacion_id")
    private habitacion habitacion;

    // cantidad reservada de ese tipo
    private int cantidad;

    // subtotal = precioPorNoche * cantidad * noches (calculado por el service)
    private Double subtotal;

    public ReservaHabitacion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public reservacion getReservacion() { return reservacion; }
    public void setReservacion(reservacion reservacion) { this.reservacion = reservacion; }

    public habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(habitacion habitacion) { this.habitacion = habitacion; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}*/

package com.hotel.demo.model;

import jakarta.persistence.*;

@Entity
public class ReservaHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private habitacion habitacionId;
    private int cantidad;
    private double subtotal;

    @ManyToOne
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    public ReservaHabitacion() {}

    public ReservaHabitacion(habitacion habitacionId, int cantidad, double subtotal, Carrito carrito) {
        this.habitacionId = habitacionId;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.carrito = carrito;
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public habitacion getHabitacionId() { return habitacionId; }
    public void setHabitacionId(habitacion h) { this.habitacionId = h; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public Carrito getCarrito() { return carrito; }
    public void setCarrito(Carrito carrito) { this.carrito = carrito; }

    public void setReservacion(reservacion reserva) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setReservacion'");
    }

    public reservacion getReservacion() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getReservacion'");
    }

   

   
}
