// src/components/AddToCartButton.tsx
import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { useCart } from '@/context/CartContext';

type Props = {
  habitacion: { id: number; tipo?: string; precioPorNoche?: number; cantidad?: number; imagenUrl?: string | null };
  fechaLlegada: string;
  fechaSalida: string;
  cantidad?: number;
  onAdded?: () => void;
  className?: string;
};

export default function AddToCartButton({
  habitacion,
  fechaLlegada,
  fechaSalida,
  cantidad = 1,
  onAdded,
  className = ''
}: Props) {
  const { addToCart } = useCart();
  const [loading, setLoading] = useState(false);

  const handleClick = async () => {
    try {
      if (!fechaLlegada || !fechaSalida) {
        alert('Selecciona fecha de llegada y fecha de salida.');
        return;
      }
      if (new Date(fechaSalida) <= new Date(fechaLlegada)) {
        alert('La fecha de salida debe ser posterior a la de llegada.');
        return;
      }
      if (cantidad < 1) {
        alert('Cantidad inválida.');
        return;
      }

      setLoading(true);
      await addToCart({
        habitacionId: habitacion.id,
        cantidad,
        fechaLlegada,
        fechaSalida
      });
      setLoading(false);
      alert('Habitación agregada al carrito');
      onAdded && onAdded();
    } catch (err: any) {
      setLoading(false);
      console.error('AddToCart error', err);
      const msg = err?.response?.data?.message || err?.message || 'Error al agregar al carrito';
      alert(msg);
    }
  };

  return (
    <Button onClick={handleClick} disabled={loading} className={className}>
      {loading ? 'Agregando...' : 'Agregar al carrito'}
    </Button>
  );
}
