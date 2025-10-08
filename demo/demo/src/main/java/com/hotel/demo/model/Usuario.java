package com.hotel.demo.model;


import jakarta.persistence.*;

@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "correo"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    private String nombre;
=======
    private String username;
    private String password;
    private boolean isAdmin = false;

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
>>>>>>> c1a0f875f92bf93d5a58ec25010063f449105279

    @Column(nullable = false, unique = true)
    private String correo;

    // Almacenamos el hash de la contraseña (no la contraseña en texto plano)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String celular;
    private String documentoIdentidad;
    private String direccion;

    // Rol (por defecto CLIENTE)
    private String rol;


    public Usuario() {}

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

}
