package com.hotel.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.hotel.demo.model.servicios;
import com.hotel.demo.service.servicioservice;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/servicios")
public class servicioscontroller {

    private final servicioservice servicioService;

    public servicioscontroller(servicioservice servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public List<servicios> listarServicios() {
        return servicioService.listarServicios();
    }

    @PostMapping
    public servicios crearServicio(@RequestBody servicios servicio) {
        return servicioService.guardarServicio(servicio);
    }

    @GetMapping("/{id}")
    public servicios obtenerServicio(@PathVariable Long id) {
        return servicioService.obtenerServicioPorId(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
    }

    @DeleteMapping("/{id}")
    public void eliminarServicio(@PathVariable Long id) {
        servicioService.eliminarServicio(id);
    }

    @PutMapping("/{id}")
    public servicios actualizarServicio(@PathVariable Long id, @RequestBody servicios servicioActualizado) {
        return servicioService.actualizarServicio(id, servicioActualizado);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> subirImagen(@RequestParam("file") MultipartFile file) {
        try {
            // Carpeta donde se guardarán las imágenes
            String carpetaDestino = "uploads/";

            // Crear la carpeta si no existe
            Path rutaCarpeta = Paths.get(carpetaDestino);
            if (!Files.exists(rutaCarpeta)) {
                Files.createDirectories(rutaCarpeta);
            }

            // Guardar el archivo
            Path rutaArchivo = rutaCarpeta.resolve(file.getOriginalFilename());
            Files.write(rutaArchivo, file.getBytes());

            // Devolver ruta para guardarla en imagenUrl
            String rutaImagen = "/uploads/" + file.getOriginalFilename(); // Puedes ajustarlo según el frontend
            return ResponseEntity.ok(rutaImagen);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la imagen");
        }
    }
}
