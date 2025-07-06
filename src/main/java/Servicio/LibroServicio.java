package Servicio;

import com.mdw.dominio.Libro;
import com.mdw.dominio.RegistroLibro;
import interfaces.LibroRepository;
import interfaces.RegistroLibroRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LibroServicio {
    @Autowired
    private LibroRepository libroRepository;
    
    @Autowired
    private RegistroLibroRepository registrolibrorepository;

    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    public Libro guardarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    public void eliminarLibro(int idLibro) {
        libroRepository.deleteById(idLibro);
    }

    public Libro obtenerLibroPorId(int idLibro) {
        return libroRepository.findById(idLibro).orElse(null);
    }
    
    public List<Libro> obtenerLibrosRecientes() {
        List<RegistroLibro> registros = registrolibrorepository.findTop8ByOrderByFechaDesc();
        List<Integer> ids = registros.stream()
            .map(r -> r.getIdLibro()) // CORREGIDO
            .toList();
        return libroRepository.findAllById(ids);
    }
}

