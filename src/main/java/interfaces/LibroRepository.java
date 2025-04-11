package interfaces;

import com.mdw.dominio.Libro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LibroRepository extends JpaRepository<Libro, Integer>{
        long countByIdUsuario(int idUsuario);
        List<Libro> findByTituloContainingIgnoreCase(String Titulo);

        List<Libro> findByIdUsuario(int idUsuario);
}
