package com.hotel.demo.service;

import com.hotel.demo.model.Usuario;
import com.hotel.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Registra un usuario. La contraseña en texto plano se recibe por parámetro
     * y se guarda como hash.
     *
     * @param usuario Usuario (sin passwordHash)
     * @param plainPassword contraseña en texto plano
     * @return Usuario guardado (con id y timestamps)
     */
    @Transactional
    public Usuario registrarUsuario(Usuario usuario, String plainPassword) {
        if (usuario.getCorreo() == null || plainPassword == null) {
            throw new IllegalArgumentException("Correo y contraseña son obligatorios.");
        }
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con ese correo.");
        }
        String hashed = passwordEncoder.encode(plainPassword);
        usuario.setPasswordHash(hashed);

        // Si el rol no está puesto, se setea en la entidad desde @PrePersist, pero
        // si quieres forzar: usuario.setRol("CLIENTE");
        return usuarioRepository.save(usuario);
    }

    /**
     * Intenta loguear con correo y contraseña en texto plano.
     * Devuelve Optional<Usuario> si las credenciales coinciden.
     */
    public Optional<Usuario> login(String correo, String plainPassword) {
        Optional<Usuario> opt = usuarioRepository.findByCorreo(correo);
        if (opt.isEmpty()) return Optional.empty();

        Usuario u = opt.get();
        if (passwordEncoder.matches(plainPassword, u.getPasswordHash())) {
            return Optional.of(u);
        }
        return Optional.empty();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
}

