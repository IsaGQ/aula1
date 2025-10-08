import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Index from "./pages/Index";
import Rooms from "./pages/Rooms";
import Services from "./pages/Services";
import Reservation from "./pages/Reservation";
import Registrarse from "./pages/Registrarse";
import NotFound from "./pages/NotFound";
import Admin from "./pages/Admin";
import Login from "./pages/Login";

import MisReservas from './pages/MisReservas';

// Importamos el contexto del carrito
import { CartProvider } from "@/context/CartContext";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      {/* 👇 Envolvemos TODA la app con CartProvider */}
      <CartProvider>
        <BrowserRouter>
          <Routes>
            {/* Redirige /admin al inicio del panel admin */}
            <Route path="/admin" element={<Navigate to="/admin/habitaciones" replace />} />

<<<<<<< HEAD
            {/* Rutas anidadas del panel admin */}
            <Route path="/admin/*" element={<Admin />} />
=======
          {/* Rutas públicas */}
          <Route path="/" element={<Index />} />
          <Route path="/habitaciones" element={<Rooms />} />
          <Route path="/servicios" element={<Services />} />
          <Route path="/reserva" element={<Reservation />} />
          <Route path="/registro" element={<Registrarse />} />
          <Route path="/login" element={<Login />} />
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279

            {/* Rutas públicas */}
            <Route path="/" element={<Index />} />
            <Route path="/habitaciones" element={<Rooms />} />
            <Route path="/servicios" element={<Services />} />
            <Route path="/reserva" element={<Reservation />} />

            {/* Página no encontrada */}
            <Route path="*" element={<NotFound />} />
            <Route path="/mis-reservas" element={<MisReservas />} />
          </Routes>
        </BrowserRouter>
      </CartProvider>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
