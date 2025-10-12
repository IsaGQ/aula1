package com.hotel.demo.dto;

public class ReservacionItemDTO {
    private Long habitacionId;
    private Integer cantidad;

    public Long getHabitacionId() { return habitacionId; }
    public void setHabitacionId(Long habitacionId) { this.habitacionId = habitacionId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
