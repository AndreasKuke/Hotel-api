package app.security;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class SecurityRoutes {

    SecurityController securityController = new SecurityController();

    public EndpointGroup getSecurityRoutes() {
        return ()->{
            path("/auth", () -> {
                post("/login", securityController.login());
            });
        };
    }
}