package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class categoriaDao {
    @PersistenceContext
    private EntityManager entitymanager;
}
