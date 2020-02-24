package org.lvelez.book;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import websockets.BookEventSocket;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("/catalog/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
    private static final Logger logger = LogManager.getLogger(BookResource.class);

    @Inject
    BookRepository bookRepository;

    @Inject
    BookEventSocket bookEventSocket;

    @GET
    public CompletionStage<Response> list() {
        CompletableFuture<Response> future = new CompletableFuture<>();
        bookRepository.list()
                .thenApply(ArrayList::new)
                .thenApply(books -> {
                    return Response.ok(books).build();
                })
                .exceptionally(throwable -> {
                    logger.log(Level.ERROR, "There was an error getting list of books. Error was: "+ throwable.getMessage());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                })
                .whenComplete((response, throwable) -> {
                    future.complete(response);
                });

        return future;
    }

    @GET
    @Path("/{id}")
    public CompletionStage<Response> findById (@PathParam("id") String id) {
        CompletableFuture<Response> future = new CompletableFuture<>();

        bookRepository.findById(id)
                .thenApply(book -> {
                    if (book.isPresent()) {
                        return Response.ok(book).build();
                    } else {
                        return Response.ok().status(204).build();
                    }
                })
                .exceptionally(throwable -> {
                    logger.log(Level.ERROR, "There was an error finding book with ISBN " + id + "Error was: "+ throwable.getMessage());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                })
                .whenComplete((response, throwable) -> {
                    future.complete(response);
                });

        return future;
    }

    @POST
    public CompletionStage<Response> add(@Valid Book book) {
        CompletableFuture<Response> future = new CompletableFuture<>();

        bookRepository.add(book)
                .thenApply(data -> {
                    return Response.ok(book.getIsbn()).status(201).build();
                })
                .exceptionally(throwable -> {
                    logger.log(Level.ERROR, "There was an error trying to add book " + book.getName() +
                            "Error was: "+ throwable.getMessage());
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                })
                .whenComplete((response, throwable) -> {
                    if (response.getStatus() == 201) {
                        bookEventSocket.broadcast("Book added : " + book.getName());
                    }
                    future.complete(response);
                });

        return future;
    }

    @PUT
    public CompletionStage<Boolean> update(@Valid Book book) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        bookRepository.update(book.getIsbn(), book)
                .thenApply(UpdateResult::wasAcknowledged)
                .exceptionally(throwable -> {
                    logger.log(Level.ERROR, "There was an error trying to update book " + book.getName() +
                            "Error was: "+ throwable.getMessage());
                    return false;
                })
                .whenComplete((ack, throwable) -> {
                    if (ack) {
                        bookEventSocket.broadcast("Book updated : " + book.getName());
                    }
                    future.complete(ack);
                });

        return future;
    }

    @DELETE
    @Path("/{id}")
    public CompletionStage<Boolean> delete (@PathParam("id") String id) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        bookRepository.delete(id)
                .thenApply(DeleteResult::wasAcknowledged)
                .exceptionally(throwable -> {
                    logger.log(Level.ERROR, "There was an error trying to delete book with ISBN " +id +
                            "Error was: "+ throwable.getMessage());
                    return false;
                })
                .whenComplete((ack, throwable) -> {
                    if (ack) {
                        bookEventSocket.broadcast("Book deleted, isbn : " +id);
                    }
                    future.complete(ack);
                });

        return future;
    }
}
