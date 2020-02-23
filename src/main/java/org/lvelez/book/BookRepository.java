package org.lvelez.book;

import io.quarkus.mongodb.ReactiveMongoClient;
import io.quarkus.mongodb.ReactiveMongoCollection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class BookRepository {

    private static final String database = "book_catalog";
    private static final String collection = "books";

    @Inject
    ReactiveMongoClient mongoClient;

    public CompletionStage<List<Book>> list(){
        return getCollection().find().toList().run();
    }

    private ReactiveMongoCollection<Book> getCollection(){
        return mongoClient.getDatabase(database).getCollection(collection, Book.class);
    }
}
