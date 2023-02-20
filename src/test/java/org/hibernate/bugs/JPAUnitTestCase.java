package org.hibernate.bugs;

import entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JPAUnitTestCase {

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

    @After
    public void destroy() {
        entityManagerFactory.close();
    }

    private static final String NATIVE_SELECT_QUERY = """
             select * from student a where (:firstname is null or a.first_name in :firstname)
            """;
    @Test
    public void nativeQueryExample() {
        EntityManager entityManager = setEntityManagerData();

        try {
            var query = entityManager.createNativeQuery(NATIVE_SELECT_QUERY);
            query.setParameter("firstname", null);
            var resultList = query.getResultList();

            assertEquals(2L, resultList.size());
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void nativeQueryExample1() {
        EntityManager entityManager = setEntityManagerData();

        try {
            var query = entityManager.createNativeQuery(NATIVE_SELECT_QUERY);
            query.setParameter("firstname", List.of());
            var resultList = query.getResultList();

            assertEquals(4L, resultList.size());
        } finally {
            entityManager.close();
        }
    }

    private EntityManager setEntityManagerData() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Student student = new Student("qwerty", "asdf", "qwerty@asdf.com");
        Student student1 = new Student("qwerty1", "asdf1", "qwerty1@asdf1.com");
        Student student2 = new Student("qwerty2", "asdf2", "qwerty2@asdf2.com");
        Student student3 = new Student("qwerty3", "asdf3", "qwerty3@asdf3.com");

        entityManager.persist(student);
        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.persist(student3);

        entityManager.getTransaction().commit();
        return entityManager;
    }
}
