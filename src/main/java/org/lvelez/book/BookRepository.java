package org.lvelez.book;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.quarkus.mongodb.ReactiveMongoClient;
import io.quarkus.mongodb.ReactiveMongoCollection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class BookRepository {

    private static final String database = "book_catalog";
    private static final String collection = "books";

    @Inject
    ReactiveMongoClient mongoClient;

    public CompletionStage<List<Book>> list(){
        return getCollection().find().toList().run();
    }

    public CompletionStage<Optional<Book>> findById(String id) {
        return getCollection().find(eq("_id", id))
                .findFirst()
                .run();
    }

    public CompletionStage<Void> add(Book book){
        return getCollection().insertOne(book);
    }

    public CompletionStage<UpdateResult> update(String id, Book book) {
        return getCollection().replaceOne(eq("_id", id), book);
    }

    public CompletionStage<DeleteResult> delete(String id) {
        return getCollection().deleteOne(eq("_id", id));
    }

    private ReactiveMongoCollection<Book> getCollection(){
        return mongoClient.getDatabase(database).getCollection(collection, Book.class);
    }
}
