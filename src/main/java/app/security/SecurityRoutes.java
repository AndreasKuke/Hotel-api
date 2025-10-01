package app.security;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class SecurityRoutes {

    SecurityController securityController = new SecurityController();

    public EndpointGroup getSecurityRoutes() {
        return ()->{
            path("/", () -> {
                post("/login", securityController.login());
                post("/create", securityController::createUser);
                put("/give-role", securityController::giveUserRole);

            });
        };
    }
}