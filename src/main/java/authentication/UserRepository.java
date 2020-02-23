package authentication;

import io.quarkus.mongodb.ReactiveMongoClient;
import io.quarkus.mongodb.ReactiveMongoCollection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class UserRepository {
    private static final String database = "book_catalog";
    private static final String collection = "users";
    @Inject
    ReactiveMongoClient mongoClient;

    public CompletionStage<Optional<User>> login(User user) {
        CompletableFuture<User> future = new CompletableFuture<>();

        return getCollection().find(eq("username", user.getUsername()))
                .findFirst()
                .run();
    }

    private ReactiveMongoCollection<User> getCollection(){
        return mongoClient.getDatabase(database).getCollection(collection, User.class);
    }
}