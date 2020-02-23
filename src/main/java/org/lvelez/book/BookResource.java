package org.lvelez.book;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}
