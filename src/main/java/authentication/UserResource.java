package authentication;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger logger = LogManager.getLogger(UserResource.class);

    @Inject
    UserRepository userRepository;

    @POST
    public CompletionStage<Boolean> login(@Valid User user) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        userRepository.login(user)
                .thenApply(loggedUser -> {
                    if(loggedUser.isPresent()) {
                        return loggedUser.map(value -> value.getPassword().equals(user.getPassword())).orElse(false);
                    } else {
                        return false;
                    }
                })
                .exceptionally(throwable -> {
                    logger.log(Level.WARN, "There was an error trying to log user " + user.getUsername()
                            + "Error was: "+ throwable.getMessage());
                    return false;
                })
                .whenComplete((result, throwable) -> {
                    future.complete(result);
                });

        return future;
    }
}