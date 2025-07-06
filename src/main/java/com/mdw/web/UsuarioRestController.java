package com.mdw.web;

import Servicio.UsuarioServicio;
import com.mdw.dominio.Usuario;
import interfaces.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioRestController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        Optional<Usuario> existente = usuarioRepository.findByUsername(usuario.getUsername());
        if (existente.isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }
        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(guardado);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(usuario.getUsername());

        if (usuarioOptional.isPresent()) {
            Usuario u = usuarioOptional.get();
            if (u.getContraseña().equals(usuario.getContraseña())) {
                return ResponseEntity.ok(u);
            } else {
                return ResponseEntity.status(401).body("Contraseña incorrecta");
            }
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioServicio.listarUsuarios());
    }

}
