package interfaces;

import com.mdw.dominio.Tipo;
import com.mdw.dominio.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TiposRepository extends JpaRepository<Tipo, Integer>{
    
}
