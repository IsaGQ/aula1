import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';

// EJEMPLO de URLs DIRECTAS a imágenes (reemplaza por tus imágenes subidas o por URLs directas válidas)
const sliderImages = [
  {
    url: "https://revistasumma.com/wp-content/uploads/2021/10/IMG_20211002_111946-scaled-1.jpg",
    alt: "San carlos"
  },
  {
    url: "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86",
    alt: "Cascada"
  },
  {
    url: "https://mediaim.expedia.com/destination/1/952771db0feeafbb044c10395f41ce1d.jpg",
    alt: "Paisaje montañoso"
  },
  {
    url: "https://cdn.pixabay.com/photo/2023/05/13/17/43/water-7991010_1280.jpg",
    alt: "Bosque"
  }
];

const HeroSlider = () => {
  const [currentSlide, setCurrentSlide] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentSlide((prev) => (prev === sliderImages.length - 1 ? 0 : prev + 1));
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="hero-slider overflow-hidden relative h-[60vh] md:h-[75vh]">
      {/* Images */}
      {sliderImages.map((image, index) => (
        <div
          key={index}
          className={`absolute inset-0 transition-opacity duration-1000 ${index === currentSlide ? 'opacity-100 z-10' : 'opacity-0 z-0'}`}
        >
          <img
            src={image.url}
            alt={image.alt}
            className="w-full h-full object-cover"
            onError={(e) => {
              // fallback si falla la carga (placeholder)
              (e.currentTarget as HTMLImageElement).src = "https://via.placeholder.com/1400x800?text=Imagen+no+disponible";
            }}
          />
          {/* overlay semitransparente */}
          <div className="absolute inset-0 bg-black bg-opacity-30 pointer-events-none"></div>
        </div>
      ))}

      {/* Overlay Content */}
      <div className="absolute inset-0 flex flex-col items-center justify-center text-center px-4 md:px-8 z-20">
        <h1 className="text-4xl md:text-6xl lg:text-7xl text-white font-bold mb-4 md:mb-6 font-playfair max-w-4xl">
          Donde la naturaleza y la diversión se encuentran
        </h1>
        <p className="text-lg md:text-xl text-white mb-8 max-w-2xl">
          Disfruta de una experiencia única en nuestro lodge ecológico, donde el lujo se encuentra con la sostenibilidad.
        </p>
        <Button size="lg" className="bg-eco-dark-green hover:bg-eco-medium-green text-white px-8 py-6 text-lg">
          Reservar Ahora
        </Button>
      </div>

      {/* Slider indicators */}
      <div className="absolute bottom-8 left-0 right-0 flex justify-center space-x-2 z-30">
        {sliderImages.map((_, index) => (
          <button
            key={index}
            className={`h-2 rounded-full transition-all duration-300 ${index === currentSlide ? 'w-8 bg-white' : 'w-2 bg-white/50'}`}
            onClick={() => setCurrentSlide(index)}
            aria-label={`Ir a slide ${index + 1}`}
          />
        ))}
      </div>
    </div>
  );
};

export default HeroSlider;
