import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Index from "./pages/Index";
import Rooms from "./pages/Rooms";
import Services from "./pages/Services";
import Reservation from "./pages/Reservation";
import NotFound from "./pages/NotFound";
import Admin from "./pages/Admin";


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

            {/* Rutas anidadas del panel admin */}
            <Route path="/admin/*" element={<Admin />} />

            {/* Rutas públicas */}
            <Route path="/" element={<Index />} />
            <Route path="/habitaciones" element={<Rooms />} />
            <Route path="/servicios" element={<Services />} />
            <Route path="/reserva" element={<Reservation />} />

            {/* Página no encontrada */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </CartProvider>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
