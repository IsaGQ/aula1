// src/services/ReservasService.ts
import axios from 'axios';

const API_BASE = 'http://localhost:9090';

export interface ReservaMin {
  id: number;
  fechaLlegada: string;
  fechaSalida: string;
  precioTotal: number;
  estado?: string;
  nombreCompleto?: string;
  correo?: string;
  celular?: string;
  direccion?: string;
  reservaHabitaciones?: Array<{
    id: number;
    cantidad: number;
    subtotal: number;
    habitacion: { id: number; tipo: string; imagenUrl?: string | null; precioPorNoche?: number };
  }>;
}

export const obtenerReservasUsuario = (userId: string | number) => {
  return axios.get<ReservaMin[]>(`${API_BASE}/api/reservaciones/usuario/${userId}`);
};
