package app.routes;

import app.security.SecurityRoutes;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final HotelRoutes hotelRoutes = new HotelRoutes();
    private final SecurityRoutes securityRoutes = new SecurityRoutes();


    public EndpointGroup getRoutes() {
        return () -> {
            get("/", ctx -> ctx.result("Hello World"));
            path("/hotel", hotelRoutes.getRoutes());
            path("/auth", securityRoutes.getSecurityRoutes());
        };
    }
}
