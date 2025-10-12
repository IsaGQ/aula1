// src/services/CarritoService.ts
import axios from 'axios';


/**
 * Configuración básica del cliente HTTP.
 * Asegúrate que el backend corra en este host:port o cámbialo aquí.
 */
const api = axios.create({
  baseURL: 'http://localhost:9090/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

/* -------------------- Tipos -------------------- */

export type ReservaHabitacionDTO = {
  id: number;
  habitacionId: number;
  tipo?: string;
  precioPorNoche?: number;
  cantidad: number;
  subtotal: number;
  imagenUrl?: string | null;
};

export type CarritoDTO = {
  id: number | null;
  userId: string | null;
  fechaLlegada?: string | null; // "yyyy-MM-dd"
  fechaSalida?: string | null;
  precioTotal?: number;
  reservaHabitaciones: ReservaHabitacionDTO[];
};

/* Payloads que el backend espera */
export type AddToCartPayload = {
  habitacionId: number;
  cantidad: number;
  fechaLlegada: string; // "yyyy-MM-dd"
  fechaSalida: string;  // "yyyy-MM-dd"
};

export type UpdateItemPayload = {
  cantidad: number;
};

/* -------------------- Funciones -------------------- */

/**
 * Obtener el carrito del usuario (crea uno vacío si no existe).
 * @param userId string (ej: localStorage.getItem('usuarioId'))
 */
export async function obtenerCarrito(userId: string): Promise<CarritoDTO> {
  try {
    const res = await api.get<CarritoDTO>(`/carrito/${encodeURIComponent(userId)}`);
    return res.data;
  } catch (err) {
    handleAxiosError(err);
    throw err;
  }
}

/**
 * Agregar un item al carrito.
 * @param userId
 * @param payload { habitacionId, cantidad, fechaLlegada, fechaSalida }
 */
export async function agregarAlCarrito(userId: string, payload: AddToCartPayload): Promise<CarritoDTO> {
  try {
    const res = await api.post<CarritoDTO>(`/carrito/${encodeURIComponent(userId)}/items`, payload);
    return res.data;
  } catch (err) {
    handleAxiosError(err);
    throw err;
  }
}

/**
 * Actualizar la cantidad de un item del carrito.
 * @param userId
 * @param reservaItemId id del ReservaHabitacion (item)
 * @param payload { cantidad }
 */
export async function actualizarItemCarrito(
  userId: string,
  reservaItemId: number,
  payload: UpdateItemPayload
): Promise<CarritoDTO> {
  try {
    const res = await api.put<CarritoDTO>(`/carrito/${encodeURIComponent(userId)}/items/${reservaItemId}`, payload);
    return res.data;
  } catch (err) {
    handleAxiosError(err);
    throw err;
  }
}

/**
 * Eliminar un item del carrito.
 * @param userId
 * @param reservaItemId
 */
export async function eliminarItemCarrito(userId: string, reservaItemId: number): Promise<CarritoDTO> {
  try {
    const res = await api.delete<CarritoDTO>(`/carrito/${encodeURIComponent(userId)}/items/${reservaItemId}`);
    return res.data;
  } catch (err) {
    handleAxiosError(err);
    throw err;
  }
}

/**
 * Confirmar carrito -> crea una reservacion en backend y vacía el carrito.
 * Según el backend que te dejé, los datos del cliente se envían como query params.
 * Si prefieres enviarlos en el body, puedo adaptar el backend y este método.
 *
 * @param userId
 * @param nombreCompleto
 * @param cedula
 * @param celular
 * @param correo
 *
 * Retorna la reservación creada (objeto `reservacion` del backend).
 */
export async function confirmarCarrito(
  userId: string,
  nombreCompleto: string,
  cedula: string,
  celular: string,
  correo: string
): Promise<any> {
  try {
    const params = new URLSearchParams({
      nombreCompleto,
      cedula,
      celular,
      correo,
    });
    const res = await api.post<any>(`/carrito/${encodeURIComponent(userId)}/confirm?${params.toString()}`);
    return res.data;
  } catch (err) {
    handleAxiosError(err);
    throw err;
  }
}

/* -------------------- Helpers -------------------- */

function handleAxiosError(err: any) {
  if (err?.response) {
    console.error('Error HTTP:', err.response.status, err.response.data);
  } else {
    console.error('Error de red o desconocido:', err.message);
  }
}


/* -------------------- Export por defecto (opcional) -------------------- */
export default {
  obtenerCarrito,
  agregarAlCarrito,
  actualizarItemCarrito,
  eliminarItemCarrito,
  confirmarCarrito,
};
