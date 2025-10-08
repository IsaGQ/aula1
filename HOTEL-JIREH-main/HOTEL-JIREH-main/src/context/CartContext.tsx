import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import type { CarritoDTO } from '@/services/CarritoService';
import {
  obtenerCarrito,
  agregarAlCarrito,
  actualizarItemCarrito,
  eliminarItemCarrito,
  confirmarCarrito
} from '@/services/CarritoService';

type CartContextType = {
  carrito: CarritoDTO | null;
  loading: boolean;
  refreshCart: () => Promise<void>;
  addToCart: (payload: {
    habitacionId: number;
    cantidad: number;
    fechaLlegada: string;
    fechaSalida: string;
  }) => Promise<void>;
  updateItem: (reservaId: number, cantidad: number) => Promise<void>;
  removeItem: (reservaId: number) => Promise<void>;
  checkout: () => Promise<void>;
};

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
  const [carrito, setCarrito] = useState<CarritoDTO | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const userId = localStorage.getItem('usuarioId');

  const refreshCart = async () => {
    if (!userId) {
      setCarrito(null);
      return;
    }
    try {
      setLoading(true);
      const res = await obtenerCarrito(userId);
      setCarrito(res); // ✅ Corregido: ya no usamos .data
    } catch (err) {
      console.error('refreshCart error', err);
      setCarrito(null);
    } finally {
      setLoading(false);
    }
  };

  const addToCart = async (payload: {
    habitacionId: number;
    cantidad: number;
    fechaLlegada: string;
    fechaSalida: string;
  }) => {
    if (!userId) throw new Error('Usuario no autenticado');
    await agregarAlCarrito(userId, payload);
    await refreshCart();
  };

  //const updateItem = async (reservaId: number, cantidad: number) => {
    //if (!userId) throw new Error('Usuario no autenticado');
    //await actualizarItemCarrito(userId, reservaId, { cantidad: Number(cantidad) } as { cantidad: number });
    //await refreshCart();
  //};

  const updateItem = async (reservaId: number, cantidad: number) => {
  if (!userId) throw new Error('Usuario no autenticado');

  // Aseguramos el tipo explícitamente
  const payload: { cantidad: number } = { cantidad: Number(cantidad) };

  await actualizarItemCarrito(userId, reservaId, payload);
  await refreshCart();
 };

  const removeItem = async (reservaId: number) => {
    if (!userId) throw new Error('Usuario no autenticado');
    await eliminarItemCarrito(userId, reservaId);
    await refreshCart();
  };

  const checkout = async () => {
    if (!userId) throw new Error('Usuario no autenticado');
    await confirmarCarrito(userId);
    await refreshCart();
  };

  useEffect(() => {
    refreshCart();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  return (
    <CartContext.Provider
      value={{ carrito, loading, refreshCart, addToCart, updateItem, removeItem, checkout }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = (): CartContextType => {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error('useCart debe usarse dentro de CartProvider');
  return ctx;
};
