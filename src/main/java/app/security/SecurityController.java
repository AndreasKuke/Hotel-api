package app.security;

import app.config.HibernateConfig;
import app.exceptions.ValidationException;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Handler;

public class SecurityController {

    ISecurityDAO dao = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
    ObjectMapper mapper = new Utils().getObjectMapper();

    public Handler login(){
        return ctx -> {
            User user = ctx.bodyAsClass(User.class);
            try {
                User verified = dao.getVerifiedUser(user.getUsername(), user.getPassword());
                ObjectNode on = mapper.createObjectNode().put("msg", "Success for user: " + verified.getUsername());
                ctx.json(on).status(200);
            }catch (ValidationException e){
                ObjectNode on = mapper.createObjectNode().put("msg", "Invalid username or password");
                ctx.json(on).status(401);
            }
        };
    }
}
