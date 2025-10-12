package com.hotel.demo.controller;

import com.hotel.demo.model.Usuario;
import com.hotel.demo.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

record RegisterRequest(
        String nombre,
        String correo,
        String password,
        String celular,
        String documentoIdentidad,
        String direccion,
        String rol // opcional: "CLIENTE" o "ADMIN"
) {
}

record RegisterResponse(boolean success, String message, Long id) {
}

record LoginRequest(String correo, String password) {
}

record LoginResponse(boolean success, Long id, String nombre, String correo, String rol, String message) {
}

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Registro de usuario (usa UsuarioService para hashear la contraseña).
     * Todos los usuarios creados por aquí tendrán rol CLIENTE por defecto.
     */
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest req) {
        try {
            if (req.correo() == null || req.password() == null) {
                return new RegisterResponse(false, "Correo y contraseña son obligatorios.", null);
            }

            Usuario u = new Usuario();
            u.setNombre(req.nombre());
            u.setCorreo(req.correo());
            u.setCelular(req.celular());
            u.setDocumentoIdentidad(req.documentoIdentidad());
            u.setDireccion(req.direccion());

            // Si se envía rol explícito lo usamos, si no, UsuarioService/entidad pondrá
            // "CLIENTE".
            if (req.rol() != null && !req.rol().isBlank()) {
                u.setRol(req.rol());
            }

            // password se pasa al service para hashear
            Usuario saved = usuarioService.registrarUsuario(u, req.password());

            return new RegisterResponse(true, "Usuario registrado correctamente.", saved.getId());
        } catch (IllegalArgumentException ex) {
            return new RegisterResponse(false, ex.getMessage(), null);
        } catch (RuntimeException ex) {
            return new RegisterResponse(false, ex.getMessage(), null);
        } catch (Exception ex) {
            return new RegisterResponse(false, "Error interno al registrar usuario.", null);
        }
    }

    /**
     * Login simple: valida credenciales y devuelve info del usuario.
     * En producción deberías devolver un token JWT aquí.
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        try {
            if (req.correo() == null || req.password() == null) {
                return new LoginResponse(false, null, null, null, null, "Correo y contraseña son obligatorios.");
            }

            Optional<Usuario> opt = usuarioService.login(req.correo(), req.password());
            if (opt.isPresent()) {
                Usuario u = opt.get();
                // aquí podrías generar y devolver un JWT; por ahora devolvemos info básica
                return new LoginResponse(true, u.getId(), u.getNombre(), u.getCorreo(), u.getRol(), "Login correcto");
            } else {
                return new LoginResponse(false, null, null, null, null, "Credenciales inválidas");
            }
        } catch (Exception ex) {
            return new LoginResponse(false, null, null, null, null, "Error interno en login");
        }
    }
}
