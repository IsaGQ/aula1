// src/context/CartContext.tsx
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
  checkout: (cliente: { nombreCompleto: string; cedula: string; celular: string; correo: string }) => Promise<void>;
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
      setCarrito(res);
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

  const updateItem = async (reservaId: number, cantidad: number) => {
    if (!userId) throw new Error('Usuario no autenticado');
    const payload: { cantidad: number } = { cantidad: Number(cantidad) };
    await actualizarItemCarrito(userId, reservaId, payload);
    await refreshCart();
  };

  const removeItem = async (reservaId: number) => {
    if (!userId) throw new Error('Usuario no autenticado');
    await eliminarItemCarrito(userId, reservaId);
    await refreshCart();
  };

  /**
   * checkout ahora recibe un objeto cliente { nombreCompleto, cedula, celular, correo }
   */
  const checkout = async (cliente: { nombreCompleto: string; cedula: string; celular: string; correo: string }) => {
    if (!userId) throw new Error('Usuario no autenticado');
    await confirmarCarrito(userId, cliente.nombreCompleto, cliente.cedula, cliente.celular, cliente.correo);
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
