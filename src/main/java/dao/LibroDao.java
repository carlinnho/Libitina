package dao;

import com.mdw.dominio.Libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class LibroDao {

    @PersistenceContext
    private EntityManager entitymanager;

    public Libro findById(int idLibro) {
        return entitymanager.find(Libro.class, idLibro);
    }

    @Transactional
    public Libro save(Libro libro) {
        entitymanager.persist(libro);
        return libro;
    }
    
    public List<Libro> findAll() {
        return entitymanager.createQuery("SELECT l FROM Libro l", Libro.class).getResultList();
    }
    
    @Transactional
    public void update(Libro libro) {
        entitymanager.merge(libro);
    }

    @Transactional
    public void delete(Libro libro) {
        entitymanager.remove(entitymanager.contains(libro) ? libro : entitymanager.merge(libro));
    }
    
        public List<Libro> findByFilter(String tipo, String[] categorias) {
        StringBuilder sql = new StringBuilder("SELECT * FROM libros WHERE 1=1");

        // Filtro por tipo
        if (tipo != null && !tipo.isEmpty()) {
            sql.append(" AND tipo = :tipo");
        }

        // Filtro por categorías
        if (categorias != null && categorias.length > 0) {
            sql.append(" AND categoria IN :categorias");
        }

        // Crear la consulta nativa
        Query query = entitymanager.createNativeQuery(sql.toString(), Libro.class);

        // Setear parámetros en la consulta
        if (tipo != null && !tipo.isEmpty()) {
            query.setParameter("tipo", tipo);
        }

        if (categorias != null && categorias.length > 0) {
            query.setParameter("categorias", List.of(categorias)); // Se pasa el array de categorías como parámetro
        }

        // Ejecutar la consulta y devolver los resultados
        return query.getResultList();
    }
}
