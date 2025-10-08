// src/pages/MisReservas.tsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { obtenerReservasUsuario, ReservaMin } from '@/services/ReservasService';
import { Button } from '@/components/ui/button';

export default function MisReservas() {
  const [reservas, setReservas] = useState<ReservaMin[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();
  const userId = localStorage.getItem('usuarioId');

  useEffect(() => {
    if (!userId) {
      alert('Necesitas iniciar sesión para ver tus reservas.');
      navigate('/login');
      return;
    }
    cargarReservas();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const cargarReservas = async () => {
    try {
      setLoading(true);
      const res = await obtenerReservasUsuario(userId!);
      setReservas(res.data);
    } catch (err) {
      console.error('Error al obtener reservas', err);
      alert('No se pudieron cargar tus reservas.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="p-6">Cargando reservas...</div>;
  if (!reservas || reservas.length === 0) return <div className="p-6">No tienes reservas aún.</div>;

  return (
    <div className="max-w-5xl mx-auto p-6">
      <h1 className="text-2xl font-semibold text-eco-dark-green mb-4">Mis Reservas</h1>

      <div className="space-y-4">
        {reservas.map((r) => (
          <div key={r.id} className="bg-white border rounded p-4 shadow-sm">
            <div className="flex justify-between items-start">
              <div>
                <h2 className="text-lg font-semibold">Reserva #{r.id} - {r.estado ?? 'PENDIENTE'}</h2>
                <p className="text-sm text-gray-600">
                  {r.nombreCompleto ? `${r.nombreCompleto} • ` : ''}{r.correo ?? ''} • {r.celular ?? ''}
                </p>
                <p className="text-sm text-gray-700 mt-2">
                  Fechas: <span className="font-medium">{r.fechaLlegada}</span> → <span className="font-medium">{r.fechaSalida}</span>
                </p>
                <p className="text-sm text-gray-700 mt-1">Total: <span className="font-semibold">{new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(r.precioTotal)}</span></p>
              </div>

              <div className="flex flex-col items-end gap-2">
                <Button onClick={() => navigate(`/reservas/${r.id}`)} className="px-3 py-1">Ver detalle</Button>
                <div className="text-sm text-gray-500">Items: {r.reservaHabitaciones?.length ?? 0}</div>
              </div>
            </div>

            {r.reservaHabitaciones && r.reservaHabitaciones.length > 0 && (
              <div className="mt-3 grid grid-cols-1 md:grid-cols-3 gap-3">
                {r.reservaHabitaciones.map((rh) => (
                  <div key={rh.id} className="flex gap-3 items-center border rounded p-2">
                    {rh.habitacion.imagenUrl && (
                      <img
                        src={rh.habitacion.imagenUrl.startsWith('http') ? rh.habitacion.imagenUrl : `http://localhost:9090${rh.habitacion.imagenUrl}`}
                        alt={rh.habitacion.tipo}
                        className="w-24 h-16 object-cover rounded"
                      />
                    )}
                    <div>
                      <div className="font-medium text-eco-dark-green">{rh.habitacion.tipo}</div>
                      <div className="text-sm text-gray-600">Cantidad: {rh.cantidad}</div>
                      <div className="text-sm text-gray-600">Subtotal: {new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(rh.subtotal)}</div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
