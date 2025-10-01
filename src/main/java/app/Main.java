package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.security.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Main {
    public static void main(String[] args) {
        ApplicationConfig.startServer(7070);

        ISecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());

        Role role = dao.createRole("ADMIN");

    }
}