import React, { useState, ChangeEvent, FormEvent } from "react";
import axios from "axios";
import { crearServicio } from "@/services/ServiciosService";

interface Servicio {
  nombre: string;
  descripcion: string;
  imagenUrl: string;
}

const CrearServicios: React.FC = () => {
  const [servicio, setServicio] = useState<Servicio>({
    nombre: "",
    descripcion: "",
    imagenUrl: "",
  });

  const [imagenFile, setImagenFile] = useState<File | null>(null);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setServicio({ ...servicio, [e.target.name]: e.target.value } as Servicio);
  };

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    setImagenFile(file || null);
  };

  const subirImagen = async (): Promise<string> => {
    if (!imagenFile) return "";
    const formData = new FormData();
    formData.append("file", imagenFile);

    try {
      // Ajusta la URL si tu endpoint es distinto
      const response = await axios.post<string>("http://localhost:9090/api/servicios/upload", formData);
      return response.data;
    } catch (error) {
      console.error("Error al subir imagen de servicio:", error);
      return "";
    }
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      const imagenUrlSubida = await subirImagen();

      const servicioConImagen: Servicio = {
        ...servicio,
        imagenUrl: imagenUrlSubida, // si no se sube archivo, usar lo que haya en input (por compatibilidad)
      };

      await crearServicio(servicioConImagen);

      alert("Servicio creado con éxito");

      setServicio({ nombre: "", descripcion: "", imagenUrl: "" });
      setImagenFile(null);
    } catch (error) {
      console.error("Error al crear servicio:", error);
      alert("Error al crear el servicio. Intenta nuevamente.");
    }
  };

  return (
    <div className="flex justify-center mt-10 px-4">
      <div className="bg-white shadow-lg rounded-xl p-8 w-full max-w-xl">
        <h2 className="text-2xl font-semibold mb-6 text-eco-dark-green text-center">Crear Servicio</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
            <input
              type="text"
              name="nombre"
              value={servicio.nombre}
              onChange={handleChange}
              required
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-eco-dark-green"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
            <input
              type="text"
              name="descripcion"
              value={servicio.descripcion}
              onChange={handleChange}
              required
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-eco-dark-green"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Imagen (archivo)</label>
            <input
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm file:mr-4 file:py-2 file:px-4 file:border-0 file:text-sm file:bg-eco-dark-green file:text-white hover:file:bg-eco-cream"
            />
          </div>

          {/* Campo opcional para pegar URL si el usuario prefiere */}
          
          <div className="flex justify-end gap-4 mt-6">
            <button
              type="submit"
              className="bg-eco-dark-green text-white px-6 py-2 rounded-md hover:bg-eco-cream hover:text-eco-dark-green transition-colors"
            >
              Crear
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CrearServicios;
