package interfaces;

import com.mdw.dominio.ERole;
import com.mdw.dominio.RoleEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long>{
        Optional<RoleEntity> findByName(ERole name);
}
