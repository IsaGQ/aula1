import React, { useEffect, useState } from 'react';
import Navbar from '@/components/Navbar';
import Footer from '@/components/Footer';
import { Button } from '@/components/ui/button';
// Ajusta la ruta al archivo que exporta obtenerHabitaciones correctamente
import { agregarAlCarrito } from '../services/CarritoService';
import { obtenerHabitaciones } from '../services/HabitaciService';
import axios from 'axios'; // por si usas directamente

const Rooms = () => {
  const [habitaciones, setHabitaciones] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedHab, setSelectedHab] = useState(null);
  const [form, setForm] = useState({
    fechaLlegada: '',
    fechaSalida: '',
    cantidad: 1
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    cargarHabitaciones();
  }, []);

  const cargarHabitaciones = async () => {
    try {
      const res = await obtenerHabitaciones();
      setHabitaciones(res.data);
    } catch (error) {
      console.error("Error al cargar habitaciones:", error);
    }
  };

  const obtenerUrlImagen = (imagenUrl) => {
    if (!imagenUrl) return null;
    return imagenUrl.startsWith("http") ? imagenUrl : `http://localhost:9090${imagenUrl}`;
  };

  const abrirModalReservar = (hab) => {
    setSelectedHab(hab);
    setForm({
      fechaLlegada: '',
      fechaSalida: '',
      cantidad: 1
    });
    setModalOpen(true);
  };

  const cerrarModal = () => {
    setModalOpen(false);
    setSelectedHab(null);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmitReserva = async (e) => {
    e.preventDefault();

    // Validaciones simples en frontend
    if (!form.fechaLlegada || !form.fechaSalida) {
      return alert('Selecciona fecha de llegada y salida.');
    }
    if (new Date(form.fechaSalida) <= new Date(form.fechaLlegada)) {
      return alert('La fecha de salida debe ser posterior a la fecha de llegada.');
    }
    if (form.cantidad < 1) {
      return alert('La cantidad debe ser al menos 1.');
    }
    if (!selectedHab) return alert('Habitación no seleccionada.');

    const userId = localStorage.getItem('usuarioId');
    if (!userId) {
      // aquí puedes redirigir a login o mostrar mensaje
      return alert('Necesitas iniciar sesión para reservar.');
    }

    const payload = {
      habitacionId: selectedHab.id,
      cantidad: parseInt(form.cantidad.toString(), 10),
      fechaLlegada: form.fechaLlegada,
      fechaSalida: form.fechaSalida
    };

    try {
      setLoading(true);
      await agregarAlCarrito(userId, payload);
      setLoading(false);
      alert('Habitación agregada al carrito correctamente.');
      cerrarModal();
      // opcional: redirigir al carrito o recargar datos
      // navigate('/cart') o window.location.href = '/cart'
    } catch (err) {
      setLoading(false);
      console.error('Error al agregar al carrito:', err);
      // manejar distintos errores según el backend (p.ej. 409 stock)
      if (err.response && err.response.data) {
        alert(err.response.data.message || 'Error al agregar al carrito.');
      } else {
        alert('Error de conexión con el servidor.');
      }
    }
  };

  return (
    <div className="min-h-screen">
      <Navbar />
      <div className="pt-24 pb-8 bg-eco-light-green bg-opacity-20">
        <div className="container mx-auto px-4">
          <h1 className="text-4xl md:text-5xl font-bold text-eco-dark-green text-center mb-4 font-playfair">
            Nuestras Habitaciones
          </h1>
          <p className="text-center text-gray-600 max-w-2xl mx-auto mb-8">
            Descubre nuestras acogedoras habitaciones diseñadas para brindarte una experiencia única en armonía con la naturaleza.
          </p>
        </div>
      </div>

      <div className="py-12 px-4">
        <div className="container mx-auto">
          {/* Filtros */}
          <div className="mb-8">
            <h2 className="text-2xl font-semibold text-eco-dark-green mb-4">Filtros</h2>
            <div className="flex flex-wrap gap-3">
              <Button variant="outline" className="border-eco-dark-green text-eco-dark-green">
                Todas
              </Button>
              <Button variant="outline" className="border-eco-dark-green text-eco-dark-green">
                Vista al Bosque
              </Button>
              <Button variant="outline" className="border-eco-dark-green text-eco-dark-green">
                Vista a la Montaña
              </Button>
              <Button variant="outline" className="border-eco-dark-green text-eco-dark-green">
                Cerca del Río
              </Button>
            </div>
          </div>

          {/* Lista de Habitaciones */}
          <div className="grid gap-8 grid-cols-1 sm:grid-cols-2 md:grid-cols-3">
            {habitaciones.map((hab) => (
              <div key={hab.id} className="bg-white shadow-md rounded-lg overflow-hidden">
                {hab.imagenUrl && (
                  <img
                    src={obtenerUrlImagen(hab.imagenUrl)}
                    alt={`Imagen de ${hab.tipo}`}
                    className="w-full h-48 object-cover"
                  />
                )}
                <div className="p-4">
                  <h3 className="text-xl font-semibold text-eco-dark-green">{hab.tipo}</h3>
                  <p className="text-gray-600 mb-2">{hab.descripcion}</p>
                  <p className="text-sm"><strong>Precio:</strong> ${hab.precioPorNoche} COP</p>
                  <p className="text-sm"><strong>Capacidad:</strong> {hab.capacidad} personas</p>

                  <div className="mt-4 flex gap-3">
                    <Button
                      onClick={() => abrirModalReservar(hab)}
                      className="bg-eco-dark-green hover:bg-eco-medium-green text-white"
                    >
                      Reservar
                    </Button>

                    <a
                      href={`/habitaciones/${hab.id}`}
                      className="inline-block border border-eco-dark-green text-eco-dark-green hover:bg-eco-dark-green hover:text-white px-4 py-2 rounded-md transition-colors"
                    >
                      Ver Detalles
                    </a>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Información adicional */}
          <div className="mt-12 bg-eco-cream p-6 rounded-lg shadow-md">
            <h2 className="text-2xl font-semibold text-eco-dark-green mb-4">Información Adicional</h2>
            <ul className="list-disc ml-6 space-y-2">
              <li>Todas nuestras habitaciones incluyen desayuno orgánico preparado con productos locales.</li>
              <li>Check-in: 15:00 hrs / Check-out: 12:00 hrs</li>
              <li>Ofrecemos traslado gratuito desde/hacia la estación de autobuses más cercana.</li>
              <li>Mascotas permitidas en habitaciones seleccionadas (consultar disponibilidad).</li>
              <li>Cancelación gratuita hasta 48 horas antes de la fecha de llegada.</li>
            </ul>
          </div>
        </div>
      </div>

      <Footer />

      {/* Modal simple */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white rounded-lg shadow-lg max-w-md w-full p-6">
            <h3 className="text-xl font-semibold text-eco-dark-green mb-4">Reservar: {selectedHab?.tipo}</h3>

            <form onSubmit={handleSubmitReserva} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Fecha llegada</label>
                <input
                  type="date"
                  name="fechaLlegada"
                  value={form.fechaLlegada}
                  onChange={handleChange}
                  className="mt-1 block w-full border rounded-md p-2"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Fecha salida</label>
                <input
                  type="date"
                  name="fechaSalida"
                  value={form.fechaSalida}
                  onChange={handleChange}
                  className="mt-1 block w-full border rounded-md p-2"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700">Cantidad</label>
                <input
                  type="number"
                  name="cantidad"
                  min="1"
                  max={selectedHab?.cantidad || 10}
                  value={form.cantidad}
                  onChange={handleChange}
                  className="mt-1 block w-full border rounded-md p-2"
                  required
                />
                <p className="text-xs text-gray-500 mt-1">Disponibles: {selectedHab?.cantidad ?? '-'}</p>
              </div>

              <div className="flex justify-end gap-2">
                <button
                  type="button"
                  onClick={cerrarModal}
                  className="px-4 py-2 border rounded-md"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-eco-dark-green text-white rounded-md"
                  disabled={loading}
                >
                  {loading ? 'Agregando...' : 'Agregar al carrito'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Rooms;
