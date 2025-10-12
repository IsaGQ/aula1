// src/components/CarItem.tsx
import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import type { ReservaHabitacionDTO } from '@/services/CarritoService';

type Props = {
  item: ReservaHabitacionDTO;
  onUpdate: (nuevaCantidad: number) => Promise<void>;
  onRemove: () => Promise<void>;
};

const formatCurrency = (value: number) =>
  new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(value);

export default function CarItem({ item, onUpdate, onRemove }: Props) {
  const [cantidad, setCantidad] = useState<number>(item.cantidad);
  const [saving, setSaving] = useState<boolean>(false);
  const [removing, setRemoving] = useState<boolean>(false);

  const handleUpdate = async () => {
    if (cantidad < 1) {
      alert('La cantidad debe ser al menos 1');
      return;
    }
    try {
      setSaving(true);
      await onUpdate(cantidad);
    } catch (err) {
      console.error(err);
      alert('Error al actualizar la cantidad.');
    } finally {
      setSaving(false);
    }
  };

  const handleRemove = async () => {
    if (!confirm('¿Eliminar este item del carrito?')) return;
    try {
      setRemoving(true);
      await onRemove();
    } catch (err) {
      console.error(err);
      alert('Error al eliminar el item.');
    } finally {
      setRemoving(false);
    }
  };

  const obtenerUrlImagen = (imagenUrl?: string | null) => {
    if (!imagenUrl) return '';
    return imagenUrl.startsWith('http') ? imagenUrl : `http://localhost:9090${imagenUrl}`;
  };

  return (
    <div className="flex gap-4 items-center border-b py-4">
      <img
        src={obtenerUrlImagen(item.imagenUrl)}
        alt={item.tipo}
        className="w-24 h-20 object-cover rounded"
      />
      <div className="flex-1">
        <div className="flex justify-between items-start">
          <div>
            <h4 className="font-semibold text-eco-dark-green">{item.tipo}</h4>
            <p className="text-sm text-gray-600">Precio por noche: {formatCurrency(item.precioPorNoche || 0)}</p>
            <p className="text-sm text-gray-600">Subtotal: {formatCurrency(item.subtotal || 0)}</p>
          </div>
          <div className="text-right">
            <button
              onClick={handleRemove}
              className="text-sm text-red-600 hover:underline mb-2"
              disabled={removing}
            >
              {removing ? 'Eliminando...' : 'Eliminar'}
            </button>
          </div>
        </div>

        <div className="mt-2 flex items-center gap-2">
          <input
            type="number"
            min={1}
            value={cantidad}
            onChange={(e) => setCantidad(Number(e.target.value))}
            className="w-20 p-1 border rounded"
          />
          <Button onClick={handleUpdate} disabled={saving}>
            {saving ? 'Guardando...' : 'Actualizar'}
          </Button>
        </div>
      </div>
    </div>
  );
}
