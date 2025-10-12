package com.hotel.demo.dto;

import java.util.List;

public class CarritoDTO {
    private Long id;
    private String userId;
    private String fechaLlegada;
    private String fechaSalida;
    private Double precioTotal;
    private List<ReservaHabitacionDTO> reservaHabitaciones;

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFechaLlegada() { return fechaLlegada; }
    public void setFechaLlegada(String fechaLlegada) { this.fechaLlegada = fechaLlegada; }

    public String getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(String fechaSalida) { this.fechaSalida = fechaSalida; }

    public Double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(Double precioTotal) { this.precioTotal = precioTotal; }

    public List<ReservaHabitacionDTO> getReservaHabitaciones() { return reservaHabitaciones; }
    public void setReservaHabitaciones(List<ReservaHabitacionDTO> reservaHabitaciones) { this.reservaHabitaciones = reservaHabitaciones; }
}
