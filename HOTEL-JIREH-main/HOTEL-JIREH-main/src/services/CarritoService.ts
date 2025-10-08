// src/services/CarritoService.ts
import axios from "axios";

const API_BASE = "http://localhost:9090"; // ajusta si tu backend corre en otro puerto o ruta

// ---------- TIPOS ----------
export interface HabitacionMin {
  id: number;
  tipo: string;
  imagenUrl?: string | null;
  precioPorNoche: number;
  capacidad?: number;
  cantidad?: number; // stock disponible
}

export interface ReservaHabitacionDTO {
  id: number;
  habitacion: HabitacionMin;
  cantidad: number;
  subtotal: number;
}

export interface CarritoDTO {
  id: number;
  fechaLlegada: string;
  fechaSalida: string;
  precioTotal: number;
  reservaHabitaciones: ReservaHabitacionDTO[];
  confirmada?: boolean;
}

// ---------- HEADERS ----------
const buildHeaders = () => {
  const token = localStorage.getItem("token");
  return token
    ? { Authorization: `Bearer ${token}`, "Content-Type": "application/json" }
    : { "Content-Type": "application/json" };
};

// ---------- SERVICIOS ----------
/** Obtener carrito del usuario */
export const obtenerCarrito = async (userId: number | string): Promise<CarritoDTO> => {
  const res = await axios.get<CarritoDTO>(`${API_BASE}/api/carrito/${userId}`, {
    headers: buildHeaders(),
  });
  return res.data;
};

/** Agregar al carrito */
export const agregarAlCarrito = async (
  userId: number | string,
  payload: { habitacionId: number; cantidad: number; fechaLlegada: string; fechaSalida: string }
): Promise<any> => {
  const res = await axios.post(`${API_BASE}/api/carrito/${userId}/agregar`, payload, {
    headers: buildHeaders(),
  });
  return res.data;
};

/** Confirmar carrito (checkout) */
export const confirmarCarrito = async (userId: number | string): Promise<any> => {
  const res = await axios.put(`${API_BASE}/api/carrito/${userId}/confirmar`, {}, {
    headers: buildHeaders(),
  });
  return res.data;
};

/** Actualizar cantidad de un item del carrito */
export const actualizarItemCarrito = async (
  userId: number | string,
  reservaHabitacionId: number,
  payload: { cantidad: number }
): Promise<any> => {
  // ✅ Enviar "payload" directamente (no { cantidad } sin definir)
  const res = await axios.put(
    `${API_BASE}/api/carrito/${userId}/item/${reservaHabitacionId}`,
    payload,
    { headers: buildHeaders() }
  );
  return res.data;
};

/** Eliminar item del carrito */
export const eliminarItemCarrito = async (
  userId: number | string,
  reservaHabitacionId: number
): Promise<any> => {
  const res = await axios.delete(`${API_BASE}/api/carrito/${userId}/item/${reservaHabitacionId}`, {
    headers: buildHeaders(),
  });
  return res.data;
};
