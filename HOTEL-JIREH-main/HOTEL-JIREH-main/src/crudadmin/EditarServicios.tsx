import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { obtenerServicio, actualizarServicio } from "../services/ServiciosService";
import axios from "axios";

type Servicio = {
  nombre: string;
  descripcion: string;
  imagenUrl?: string | null;
};

const EditarServicios: React.FC = () => {
  const { id } = useParams<{ id?: string }>();
  const navigate = useNavigate();

  const [servicio, setServicio] = useState<Servicio>({
    nombre: "",
    descripcion: "",
    imagenUrl: ""
  });

  const [imagenFile, setImagenFile] = useState<File | null>(null);

  useEffect(() => {
    const cargarServicio = async () => {
      if (!id) return;
      try {
        const res = await obtenerServicio(id);
        const data = (res.data ?? res) as Servicio; // por si tu servicio devuelve res.data o directamente el objeto
        setServicio({
          nombre: data.nombre ?? "",
          descripcion: data.descripcion ?? "",
          imagenUrl: data.imagenUrl ?? ""
        });
      } catch (error) {
        console.error("Error al cargar el servicio:", error);
      }
    };

    cargarServicio();
  }, [id]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setServicio((prev) => ({ ...prev, [name]: value } as Servicio));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] ?? null;
    setImagenFile(file);
  };

  const subirImagen = async (): Promise<string> => {
    if (!imagenFile) return servicio.imagenUrl ?? ""; // no cambia si no hay archivo

    const formData = new FormData();
    formData.append("file", imagenFile);

    try {
      const response = await axios.post<string>("http://localhost:9090/api/servicios/upload", formData);
      return response.data;
    } catch (error) {
      console.error("Error al subir la imagen del servicio:", error);
      return servicio.imagenUrl ?? "";
    }
  };

  const obtenerUrlImagen = (imagenUrl?: string | null) => {
    if (!imagenUrl) return null;
    return imagenUrl.startsWith("http") ? imagenUrl : `http://localhost:9090${imagenUrl}`;
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!id) return;

    try {
      const imagenUrlSubida = await subirImagen();

      const servicioActualizado = {
        ...servicio,
        imagenUrl: typeof imagenUrlSubida === "string" ? imagenUrlSubida : String(imagenUrlSubida),
      };

      await actualizarServicio(id, servicioActualizado);
      alert("Servicio actualizado con éxito");
      navigate("/admin/servicios");
    } catch (error) {
      console.error("Error al actualizar el servicio:", error);
      alert("Ocurrió un error al actualizar el servicio. Intenta nuevamente.");
    }
  };

  return (
    <div className="flex justify-center py-10 px-4">
      <div className="bg-white shadow-md rounded-xl p-6 w-full max-w-lg">
        <h2 className="text-2xl font-semibold mb-6 text-eco-dark-green text-center">Editar Servicio</h2>
        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block mb-1 font-medium text-gray-700">Nombre</label>
            <input
              type="text"
              name="nombre"
              value={servicio.nombre}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-eco-dark-green"
            />
          </div>

          <div>
            <label className="block mb-1 font-medium text-gray-700">Descripción</label>
            <input
              type="text"
              name="descripcion"
              value={servicio.descripcion}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-eco-dark-green"
            />
          </div>

          <div>
            <label className="block mb-2 font-medium text-gray-700">Imagen actual</label>
            {servicio.imagenUrl && (
              <img
                src={obtenerUrlImagen(servicio.imagenUrl) || undefined}
                alt="Imagen actual"
                className="w-full h-48 object-cover rounded-md mb-4"
              />
            )}

            <label className="block mb-1 font-medium text-gray-700">Cambiar imagen (opcional)</label>
            <input
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="block w-full text-sm text-gray-600 file:mr-4 file:py-2 file:px-4 file:border-0 file:text-sm file:font-semibold file:bg-eco-dark-green file:text-white hover:file:bg-eco-cream hover:file:text-eco-dark-green cursor-pointer rounded-md"
            />
          </div>

          <button
            type="submit"
            className="w-full bg-eco-dark-green text-white py-2 rounded-md font-semibold hover:bg-eco-cream hover:text-eco-dark-green transition-colors"
          >
            Actualizar
          </button>
        </form>
      </div>
    </div>
  );
};

export default EditarServicios;
