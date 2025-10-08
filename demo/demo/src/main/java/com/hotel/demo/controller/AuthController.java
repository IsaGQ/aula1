package com.hotel.demo.controller;

<<<<<<< HEAD
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
=======
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.hotel.demo.config.JwtUtil;
import com.hotel.demo.dto.RegisterForm;
import com.hotel.demo.model.Cliente;
import com.hotel.demo.model.Usuario;
import com.hotel.demo.repository.UsuarioRepository;
import com.hotel.demo.service.AuthService;

import jakarta.transaction.Transactional;

import com.hotel.demo.repository.ClienteRepository;
import com.hotel.demo.dto.UsuarioDTO;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthService authService;

    private static final JwtUtil jwtUtil = new JwtUtil();

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterForm registerForm) {
        Usuario existingUser = usuarioRepository.findByUsername(registerForm.getUsername());

        if (existingUser != null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", 400);
            error.put("message", "El usuario ya existe");
            return ResponseEntity.badRequest().body(error);
        }

        Usuario usuario = new Usuario();
        usuario.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        usuario.setUsername(registerForm.getUsername());
        System.out.println(registerForm.getUsername());

        Cliente cliente = new Cliente();
        cliente.setNombreCompleto(registerForm.getNombreCompleto());
        cliente.setCelular(registerForm.getCelular());
        cliente.setCorreo(registerForm.getCorreo());
        cliente.setDireccion(registerForm.getDireccion());
        cliente.setUsuario(usuario);

        usuarioRepository.save(usuario);
        clienteRepository.save(cliente);

        Map<String, Object> success = new HashMap<>();
        success.put("status", 201);
        success.put("message", "Usuario registrado correctamente");
        return ResponseEntity.status(HttpStatus.CREATED).body(success);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername());

        if (usuario == null || !passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales incorrectas"));
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279
        }

        String token = jwtUtil.generateToken(usuario.getUsername());
        UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.getId(), usuario.getUsername(), usuario.getUsername());

        return ResponseEntity.ok(Map.of(
                "message", "Acceso correcto",
                "token", token,
                "usuario", usuarioDTO
        ));
    }

    @GetMapping("/isAuthenticated/{token}")
    public ResponseEntity<?> isAuthenticated(@PathVariable String token) {
        if (authService.isAuthenticated(token)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().body(Map.of("message", "Usuario no autenticado"));
    }

}
