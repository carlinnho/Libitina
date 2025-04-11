package interfaces;

import com.mdw.dominio.RegistroLibro;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroLibroRepository extends JpaRepository<RegistroLibro, Integer> {
    List<RegistroLibro> findTop4ByOrderByFechaDesc();
    
    @Transactional
    @Modifying
    @Query("DELETE FROM RegistroLibro r WHERE r.idLibro = :idLibro")
    void deleteAllByIdLibro(@Param("idLibro") int idLibro);
}
