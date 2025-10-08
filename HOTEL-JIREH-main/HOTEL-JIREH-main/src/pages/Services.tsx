import React, { useEffect, useState } from 'react';
import Navbar from '@/components/Navbar';
import Footer from '@/components/Footer';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Check } from 'lucide-react';
import { obtenerServicios } from '../services/ServiciosService'; // ruta correcta según tu estructura

type Servicio = {
  id?: number;
  imagenUrl?: string | null;
  nombre: string;
  descripcion: string;
  caracteristicas?: string[];
};

const Services: React.FC = () => {
  const [ecoServices, setEcoServices] = useState<Servicio[]>([]);

  useEffect(() => {
    const fetchServicios = async () => {
      try {
        const res = await obtenerServicios();
        setEcoServices(res.data as Servicio[]);
      } catch (error) {
        console.error("Error al cargar servicios:", error);
      }
    };

    fetchServicios();
  }, []);

  const obtenerUrlImagen = (imagenUrl?: string | null) => {
    if (!imagenUrl) return null;
    // Si ya es absoluta (http/https) la devolvemos tal cual
    if (imagenUrl.startsWith("http")) return imagenUrl;
    // Si viene relativa (p. ej. /uploads/...), agregamos host del backend
    return `http://localhost:9090${imagenUrl}`;
  };

  return (
    <div className="min-h-screen">
      <Navbar />
      <div className="pt-24 pb-8 bg-eco-light-green bg-opacity-20">
        <div className="container mx-auto px-4">
          <h1 className="text-4xl md:text-5xl font-bold text-eco-dark-green text-center mb-4 font-playfair">
            Nuestros Servicios
          </h1>
          <p className="text-center text-gray-600 max-w-2xl mx-auto mb-8">
            En Eco Lodge ofrecemos una variedad de servicios pensados para enriquecer tu estancia mientras respetamos y celebramos el entorno natural.
          </p>
        </div>
      </div>

      <div className="py-12 px-4">
        <div className="container mx-auto">
          {ecoServices.length === 0 ? (
            <p className="text-center text-gray-600">No hay servicios registrados.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              {ecoServices.map((service) => {
                const imgSrc = obtenerUrlImagen(service.imagenUrl);
                return (
                  <Card key={service.id ?? service.nombre} className="overflow-hidden border-eco-medium-green">
                    <div className="h-48 overflow-hidden bg-gray-100 flex items-center justify-center">
                      {imgSrc ? (
                        <img
                          src={imgSrc}
                          alt={service.nombre}
                          className="w-full h-full object-cover transition-transform hover:scale-105"
                          onError={(e) => {
                            // si la imagen no carga, mostramos un placeholder visual
                            (e.currentTarget as HTMLImageElement).src = "https://via.placeholder.com/600x400?text=Sin+imagen";
                          }}
                        />
                      ) : (
                        <img
                          src="https://via.placeholder.com/600x400?text=Sin+imagen"
                          alt="Sin imagen"
                          className="w-full h-full object-cover"
                        />
                      )}
                    </div>

                    <CardHeader>
                      <CardTitle className="text-eco-dark-green">{service.nombre}</CardTitle>
                      <CardDescription>{service.descripcion}</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <ul className="space-y-2">
                        {(service.caracteristicas || []).map((caracteristica, idx) => (
                          <li key={idx} className="flex items-start">
                            <Check className="h-5 w-5 text-eco-dark-green mr-2 shrink-0" />
                            <span>{caracteristica}</span>
                          </li>
                        ))}
                      </ul>
                    </CardContent>
                  </Card>
                );
              })}
            </div>
          )}

          <div className="mt-16 bg-eco-cream p-6 rounded-lg shadow-md">
            <h2 className="text-2xl font-semibold text-eco-dark-green mb-4">Información Adicional</h2>
            <ul className="list-disc ml-6 space-y-2">
              <li>Todos nuestros servicios están diseñados bajo estrictos principios de sostenibilidad.</li>
              <li>Ofrecemos paquetes personalizados que combinan diferentes servicios.</li>
              <li>Las actividades pueden reservarse con anticipación o durante tu estancia, sujetas a disponibilidad.</li>
              <li>Consulta nuestras tarifas especiales para huéspedes del hotel.</li>
              <li>Solicita información sobre servicios adicionales en la recepción del hotel.</li>
            </ul>
          </div>
        </div>
      </div>

      <Footer />
    </div>
  );
};

export default Services;
