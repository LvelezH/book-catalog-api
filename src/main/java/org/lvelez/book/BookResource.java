package org.lvelez.book;

import com.mongodb.client.result.UpdateResult;

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
    @Inject
    BookRepository bookRepository;

    @GET
    public CompletionStage<Response> list() {
        CompletableFuture<Response> future = new CompletableFuture<>();
        bookRepository.list()
                .thenApply(ArrayList::new)
                .thenApply(books -> {
                    return Response.ok(books).build();
                })
                .exceptionally(throwable -> {
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
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                })
                .whenComplete((response, throwable) -> {
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
                    String e = throwable.getMessage();
                    return false;
                })
                .whenComplete((ack, throwable) -> {
                    future.complete(ack);
                });

        return future;
    }
}
