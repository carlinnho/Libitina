package Servicio;

import com.mdw.dominio.Libro;
import interfaces.LibroRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LibroServicio {
    @Autowired
    private LibroRepository libroRepository;

    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    public void guardarLibro(Libro libro) {
        libroRepository.save(libro);
    }

    public void eliminarLibro(int idLibro) {
        libroRepository.deleteById(idLibro);
    }

    public Libro obtenerLibroPorId(int idLibro) {
        return libroRepository.findById(idLibro).orElse(null);
    }
}
