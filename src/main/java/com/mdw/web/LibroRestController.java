package com.mdw.web;

import Servicio.LibroServicio;
import com.mdw.dominio.Libro;
import com.mdw.dominio.RegistroLibro;
import interfaces.RegistroLibroRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "http://localhost:4200")

public class LibroRestController {

    @Autowired
    private LibroServicio libroServicio;
    
    @Autowired
    private RegistroLibroRepository registroLibroRepository; 

    @GetMapping(produces = "application/json")
    public List<Libro> obtenerLibros() {
        System.out.println("✅ ENTRO A /api/libros");
        return libroServicio.listarLibros();
    }

    @GetMapping(value = "/recientes", produces = "application/json")
    public List<Libro> obtenerLibrosRecientes() {
        System.out.println("✅ ENTRO A /api/libros/recientes");
        return libroServicio.obtenerLibrosRecientes();
    }
    

    @PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirLibro(
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("url") String url,
            @RequestParam("categoria") String categoria,
            @RequestParam("tipo") String tipo,
            @RequestParam("archivoPortada") MultipartFile archivoPortada,
            @RequestParam("idUsuario") int idUsuario) {

        try {

            if (url != null && url.contains("drive.google.com") && url.contains("/view?usp=sharing")) {
                url = url.replace("/view?usp=sharing", "/preview");
            }

            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setDescripción(descripcion);
            libro.setUrl(url);
            libro.setCategoria(categoria);
            libro.setTipo(tipo);
            libro.setPortada(archivoPortada.getBytes());
            libro.setIdUsuario(idUsuario);

            Libro libroGuardado = libroServicio.guardarLibro(libro);
            
            RegistroLibro registro = new RegistroLibro();
            registro.setIdLibro(libroGuardado.getID());
            registro.setFecha(LocalDateTime.now());
            
            registroLibroRepository.save(registro);

            return ResponseEntity.ok("Libro subido correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al subir libro");
        }
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Libro> obtenerLibroPorId(@PathVariable int id) {
        Libro libro = libroServicio.obtenerLibroPorId(id);
        if (libro != null) {
            return ResponseEntity.ok(libro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
