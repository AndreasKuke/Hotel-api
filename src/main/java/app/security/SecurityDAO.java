package app.security;

import app.config.HibernateConfig;
import app.exceptions.EntityNotFoundException;
import app.exceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class SecurityDAO implements ISecurityDAO {
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public SecurityDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }
    @Override
    public User getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = emf.createEntityManager()) {
            User foundUser = em.find(User.class, username);
            if (foundUser != null && foundUser.verifyPassword(password)) {
                return foundUser;
            } else {
                throw new ValidationException("Username or Password was incorrect");
            }
        }
    }

    @Override
    public User createUser(String username, String password) {
        try(EntityManager em = emf.createEntityManager()) {
            User user = new User(username, password);
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
    }

    @Override
    public Role createRole(String roleName) {
        Role role = new Role(roleName);
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
            return role;
        }
    }

    @Override
    public User addUserRole(String username, String role) throws EntityNotFoundException {
        try(EntityManager em = emf.createEntityManager()) {
            User foundUser = em.find(User.class, username);
            Role foundRole = em.find(Role.class, role);

            if (foundUser == null || foundRole == null) {
                throw new EntityNotFoundException("User or Role not found");
            }
            em.getTransaction().begin();
            foundUser.addRole(foundRole);
            em.getTransaction().commit();
            return foundUser;
        }
    }


    public static void main(String[] args) {
        ISecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
//        User user = dao.createUser("Hans", "Pass123");
//        System.out.println(user.getUsername() + ": " + user.getPassword());

        Role role = dao.createRole("User");

        try {
            dao.addUserRole("Hans", "User");
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            User validatedUser = dao.getVerifiedUser("Hans", "Pass123");
            System.out.println(validatedUser.getUsername() + ": " + validatedUser.getPassword());
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

    }
}
