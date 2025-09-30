package app.config;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class ErrorHandlingConfig {
    public static void registerExceptionHandlers(Javalin app) {
        // Handle bad JSON or bad state
        app.exception(IllegalStateException.class, (e, ctx) -> {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.json(new ErrorResponse("Invalid hotel/room JSON: " + e.getMessage()));
        });

        // Catch-all for anything else
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.json(new ErrorResponse("Server error: " + e.getMessage()));
        });
    }

    // Simple error response class
    static class ErrorResponse {
        public String error;
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}