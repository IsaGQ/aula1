// src/pages/Cart.tsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  obtenerCarrito,
  confirmarCarrito,
  actualizarItemCarrito,
  eliminarItemCarrito,
  type CarritoDTO,
  type ReservaHabitacionDTO
} from '@/services/CarritoService';
import CarItem from '@/components/CarItem';
import { Button } from '@/components/ui/button';

export default function Cart() {
  const [carrito, setCarrito] = useState<CarritoDTO | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [confirmando, setConfirmando] = useState<boolean>(false);
  const navigate = useNavigate();

  const userId = localStorage.getItem('usuarioId');

  useEffect(() => {
    if (!userId) {
      alert('Necesitas iniciar sesión para ver tu carrito.');
      navigate('/login');
      return;
    }
    cargarCarrito();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const cargarCarrito = async () => {
    try {
      setLoading(true);
      // obtenerCarrito devuelve CarritoDTO
      const carritoDto = await obtenerCarrito(userId!);
      setCarrito(carritoDto);
    } catch (err) {
      console.error('Error obtener carrito', err);
      alert('No se pudo cargar tu carrito.');
    } finally {
      setLoading(false);
    }
  };

  // Actualizar item: ahora enviamos { cantidad: number } como payload
  const handleUpdateItem = async (reservaId: number, cantidad: number) => {
    try {
      // aseguramos que cantidad sea number y construimos el payload
      const payload = { cantidad: Number(cantidad) };
      await actualizarItemCarrito(userId!, reservaId, payload);
      await cargarCarrito();
    } catch (err) {
      console.error('Error actualizar item', err);
      alert('No se pudo actualizar la cantidad.');
      throw err;
    }
  };

  const handleRemoveItem = async (reservaId: number) => {
    if (!confirm('¿Eliminar este item del carrito?')) return;
    try {
      await eliminarItemCarrito(userId!, reservaId);
      await cargarCarrito();
    } catch (err) {
      console.error(err);
      alert('No se pudo eliminar el item.');
    }
  };

  const handleConfirm = async () => {
    if (!carrito) return;
    if (!confirm('¿Confirmar la reserva y pagar?')) return;

    try {
      setConfirmando(true);
      await confirmarCarrito(userId!);
      alert('Reserva confirmada. Revisa tu correo para más detalles.');
      await cargarCarrito();
      navigate('/mis-reservas');
    } catch (err) {
      console.error('Error al confirmar', err);
      alert('No se pudo confirmar la reserva.');
    } finally {
      setConfirmando(false);
    }
  };

  if (loading) return <div className="p-8">Cargando carrito...</div>;
  if (!carrito || !carrito.reservaHabitaciones || carrito.reservaHabitaciones.length === 0) {
    return <div className="p-8">Tu carrito está vacío.</div>;
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h2 className="text-2xl font-semibold text-eco-dark-green mb-4">Tu Carrito</h2>

      <div className="bg-white p-4 rounded shadow">
        {carrito.reservaHabitaciones.map((rh: ReservaHabitacionDTO) => (
          <CarItem
            key={rh.id}
            item={rh}
            onUpdate={(cantidad) => handleUpdateItem(rh.id, cantidad)}
            onRemove={() => handleRemoveItem(rh.id)}
          />
        ))}

        <div className="flex justify-between items-center mt-4">
          <div>
            <p className="text-gray-600">Fechas: {carrito.fechaLlegada} → {carrito.fechaSalida}</p>
            <p className="text-lg font-semibold">
              {new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(carrito.precioTotal)}
            </p>
          </div>

          <div className="flex gap-3">
            <Button onClick={() => navigate('/habitaciones')} className="border">Seguir comprando</Button>
            <Button onClick={handleConfirm} className="bg-eco-dark-green text-white" disabled={confirmando}>
              {confirmando ? 'Confirmando...' : 'Confirmar reserva'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
