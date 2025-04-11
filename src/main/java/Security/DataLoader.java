package Security;

import com.mdw.dominio.ERole;
import com.mdw.dominio.RoleEntity;
import com.mdw.dominio.Usuario;
import interfaces.RoleRepository;
import interfaces.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(RoleRepository roleRepository, UsuarioRepository usuarioRepository) {
        return args -> {
            if (roleRepository.findByName(ERole.ADMIN).isEmpty()) {
                roleRepository.save(new RoleEntity(null, ERole.ADMIN));
                roleRepository.save(new RoleEntity(null, ERole.USER));
                roleRepository.save(new RoleEntity(null, ERole.INVITED)); // Crear el nuevo rol
            }

            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setCorreo("admin@example.com");
                admin.setContraseña("admin123"); // Contraseña sin encriptar
                admin.setRoles(Set.of(roleRepository.findByName(ERole.ADMIN).get()));

                usuarioRepository.save(admin);
            }
        };
    }

}
