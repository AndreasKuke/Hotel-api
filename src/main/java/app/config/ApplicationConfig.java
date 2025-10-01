package app.config;

import app.dtos.UserDTO;
import app.routes.Routes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import app.config.ErrorHandlingConfig;
import app.exceptions.ApiException;
import java.util.Set;
import app.security.SecurityController;

public class ApplicationConfig {



    private static Routes   routes = new Routes();

    public static void configuration(JavalinConfig config){
        config.showJavalinBanner = false;
        config.bundledPlugins.enableRouteOverview("/routes");
        config.router.contextPath = "/api/v1"; // base path for all endpoints
        config.router.apiBuilder(routes.getRoutes());
    }

    public static Javalin startServer(int port) {
        routes = new Routes();
        var app = Javalin.create(ApplicationConfig::configuration);
        ErrorHandlingConfig.registerExceptionHandlers(app); // Added error handling?
        app.start(port);

//        // Security filter
//        app.beforeMatched(ctx -> { // Before matched is different from before, in that it is not called for 404 etc.
//            if (ctx.routeRoles().isEmpty()) // no roles were added to the route endpoint so OK
//                return;
//            // 1. Get permitted roles
//            Set<String> allowedRoles = ctx.routeRoles().stream().map(role -> role.toString().toUpperCase()).collect(Collectors.toSet());
//            if (allowedRoles.contains("ANYONE")) {
//                return;
//            }
//            // 2. Get user roles
//            UserDTO user = ctx.attribute("user"); // the User was put in the context by the SecurityController.authenticate method (in a before filter on the route)
//
//            // 3. Compare
//            if(user == null)
//                ctx.status(HttpStatus.FORBIDDEN)
//                        .json(jsonMapper.createObjectNode()
//                                .put("msg", "Not authorized. No username was added from the token"));
//
//            if(!SecurityController.getInstance().authorize(user, allowedRoles)) {
//                // throw new UnAuthorizedResponse(); // version 6 migration guide
//                throw new ApiException(HttpStatus.FORBIDDEN.getCode(), "Unauthorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
//            }
//        });

        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
