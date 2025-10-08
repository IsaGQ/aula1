import axios from "axios";

export interface Habitacion {
  tipo: string;
  descripcion: string;
  precioPorNoche: string | number;
  capacidad: string | number;
  cantidad: string | number;
  imagenUrl: string;
}

// instancia axios con baseURL (no poner header global content-type para no romper FormData)
const api = axios.create({
  baseURL: "http://localhost:9090/api",
  timeout: 10000,
});

const toApiBody = (h: Habitacion) => ({
  tipo: h.tipo,
  descripcion: h.descripcion,
  precioPorNoche: typeof h.precioPorNoche === "string" ? Number(h.precioPorNoche) : h.precioPorNoche,
  capacidad: typeof h.capacidad === "string" ? Number(h.capacidad) : h.capacidad,
  cantidad: typeof h.cantidad === "string" ? Number(h.cantidad) : h.cantidad,
  imagenUrl: h.imagenUrl || "",
});

// CRUD
export const obtenerHabitaciones = () => api.get<Habitacion[]>("/habitaciones");
export const obtenerHabitacion = (id: string | number) => api.get<Habitacion>(`/habitaciones/${id}`);
export const crearHabitacion = (habitacion: Habitacion) => api.post("/habitaciones", toApiBody(habitacion));
export const actualizarHabitacion = (id: string | number, habitacion: Habitacion) =>
  api.put(`/habitaciones/${id}`, toApiBody(habitacion));
export const eliminarHabitacion = (id: string | number) => api.delete(`/habitaciones/${id}`);

// Subida de imagen (recibe File y devuelve la ruta que devuelve el backend)
export const uploadImagen = async (file: File): Promise<string> => {
  const form = new FormData();
  form.append("file", file);

  // NO establecer Content-Type aquí: dejar que el navegador ponga el boundary correct
  const res = await api.post("/habitaciones/upload", form);
  return res.data as string;
};
