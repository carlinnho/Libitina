package dao;

import com.mdw.dominio.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDao {

    @PersistenceContext
    private EntityManager entitymanager;

    public Usuario findById(int id) {
        return entitymanager.find(Usuario.class, id);
    }

    @Transactional
    public void save(Usuario usuario) {
        entitymanager.persist(usuario);
    }

    @Transactional
    public void update(Usuario usuario) {
        entitymanager.merge(usuario);
    }

    @Transactional
    public void delete(Usuario usuario) {
        entitymanager.remove(entitymanager.contains(usuario) ? usuario
                : entitymanager.merge(usuario));

    }

    public Usuario findByUsernameAndPassword(String username, String contraseña) {
        try {
            return entitymanager.createQuery("SELECT u FROM Usuario u WHERE u.username = :username AND u.contraseña = :contraseña", Usuario.class)
                    .setParameter("username", username)
                    .setParameter("contraseña", contraseña)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Retorna null si no encuentra el usuario
        }
    }

    public Usuario findByUsername(String username) {
        try {
            return entitymanager.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Retorna null si no encuentra el usuario
        }
    }

}
