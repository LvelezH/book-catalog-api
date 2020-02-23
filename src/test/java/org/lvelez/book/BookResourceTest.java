package org.lvelez.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookResourceTest {
    @Mock
    BookRepository repository;

    @Mock
    UpdateResult updateResult;

    @Mock
    DeleteResult deleteResult;

    @InjectMocks
    BookResource resource;

    private static List<Book> bookList;
    private static Book sampleBook;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void setUp() {
        bookList = ImmutableList
                .of(new Book("isbn", "name", BookGenre.ACTION, "author", "description", "language", 120),
                        new Book("2", "book 2", BookGenre.THRILLER, "author", "saddsadsa", "esperanto", 1120),
                        new Book("book3", "another book", BookGenre.ROMANCE, "other author", "bla balb al", "english", 10),
                        new Book("book 4", "and another", BookGenre.CLASSIC, "author", "desc", "spanish", 8544));

        sampleBook = new Book("isbn", "name", BookGenre.ACTION, "author", "description", "language", 120);
    }

    @Test
    public void shouldGetListOfBooks() throws ExecutionException, InterruptedException {
        givenStoredBooksInTheDatabase();
        CompletionStage<Response> response = whenGettingTheListOfBooks();
        thenBooksRetrievedAreCorrect(response);
    }

    @Test
    public void shouldReturn500IxExceptionIsThrownInRepositoryWhenGettingAllBooks() throws ExecutionException, InterruptedException {
        givenRepositoryThrowsExceptionWhenGettingBooks();
        CompletionStage<Response> response = whenGettingTheListOfBooks();
        thenTheErrorResponseIsCorrect(response);
    }

    @Test
    public void shouldGetBookByIdIfBookIsInTheDatabase() throws ExecutionException, InterruptedException {
        givenABookInTheDatabase();
        CompletionStage<Response> response = whenGettingTheBook(sampleBook.getIsbn());
        thenTheBookRetrievedIsCorrect(response);
    }

    @Test
    public void shouldReturnNoContentIfBookIsNotInTheDatabase() throws ExecutionException, InterruptedException {
        givenABookIsNotTheDatabase();
        CompletionStage<Response> response = whenGettingTheBook(sampleBook.getIsbn());
        thenTheEmptyResponseIsCorrect(response);
    }

    @Test
    public void shouldReturn500IxExceptionIsThrownInRepositoryWhenGettingBookById() throws ExecutionException, InterruptedException {
        givenRepositoryThrowsExceptionWheFindingABook();
        CompletionStage<Response> response = whenGettingTheBook(sampleBook.getIsbn());
        thenTheErrorResponseIsCorrect(response);
    }

    @Test
    public void shouldAddABookForValidBook() throws ExecutionException, InterruptedException {
        givenaRepository();
        CompletionStage<Response> response =  whenAddingABook();
        thenBookIsAddedCorrectly(response);
    }

    @Test
    public void shouldReturn500IfExceptionIsThrownInRepositoryWhenAddingABook() throws ExecutionException, InterruptedException {
        givenRepositoryThrowsExceptionWhenAddingABook();
        CompletionStage<Response> response =  whenAddingABook();
        thenTheErrorResponseIsCorrect(response);
    }

    @Test
    public void shouldUpdateABookCorrectly() throws ExecutionException, InterruptedException {
        givenABookInTheDatabaseForUpdating();
        CompletionStage<Boolean> response = whenUpdatingTheBook();
        thenTheDatabaseIsUpdated(response);
    }

    @Test
    public void shouldReturnFalseIfExceptionIsThrownInRepositoryWhenUpdatingABook() throws ExecutionException, InterruptedException {
        givenRepositoryThrowsExceptionWhenUpdatingABook();
        CompletionStage<Boolean> response = whenUpdatingTheBook();
        thenTheDatabaseIsNotUpdated(response);
    }

    @Test
    public void shouldDeleteABookCorrectly() throws ExecutionException, InterruptedException {
        givenABookInTheDatabaseForDeleting();
        CompletionStage<Boolean> response = whenDeletingTheBook();
        thenTheDatabaseIsUpdated(response);
    }


    @Test
    public void shouldReturnFalseIfExceptionIsThrownInRepositoryWhenDeletingABook() throws ExecutionException, InterruptedException {
        givenRepositoryThrowsExceptionWhenDeletingABook();
        CompletionStage<Boolean> response = whenDeletingTheBook();
        thenTheDatabaseIsNotUpdated(response);
    }
    private void givenaRepository() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.complete(null);
        when(repository.add(sampleBook)).thenReturn(future);
    }

    private void givenABookInTheDatabase() {
        CompletableFuture<Optional<Book>> future = new CompletableFuture<>();
        future.complete(Optional.of(sampleBook));

        when(repository.findById(sampleBook.getIsbn())).thenReturn(future);
    }

    private void givenABookInTheDatabaseForUpdating() {
        CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        when(updateResult.wasAcknowledged()).thenReturn(true);

        future.complete(updateResult);

        when(repository.update(any(), any())).thenReturn(future);
    }

    private void givenABookInTheDatabaseForDeleting() {
        CompletableFuture<DeleteResult> future = new CompletableFuture<>();
        when(deleteResult.wasAcknowledged()).thenReturn(true);

        future.complete(deleteResult);

        when(repository.delete(any())).thenReturn(future);
    }

    private void givenABookIsNotTheDatabase() {
        CompletableFuture<Optional<Book>> future = new CompletableFuture<>();
        future.complete(Optional.empty());

        when(repository.findById(anyString())).thenReturn(future);
    }

    private void givenStoredBooksInTheDatabase() {
        CompletableFuture<List<Book>> future = new CompletableFuture<>();
        future.complete(bookList);

        when(repository.list()).thenReturn(future);
    }

    private void givenRepositoryThrowsExceptionWhenGettingBooks() {
        CompletableFuture<List<Book>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException());
        when(repository.list()).thenReturn(future);
    }

    private void givenRepositoryThrowsExceptionWheFindingABook() {
        CompletableFuture<Optional<Book>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException());
        when(repository.findById(anyString())).thenReturn(future);
    }

    private void givenRepositoryThrowsExceptionWhenAddingABook() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException());
        when(repository.add(any())).thenReturn(future);
    }

    private void givenRepositoryThrowsExceptionWhenUpdatingABook() {
        CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException());
        when(repository.update(any(), any())).thenReturn(future);
    }

    private void givenRepositoryThrowsExceptionWhenDeletingABook() {
        CompletableFuture<DeleteResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException());
        when(repository.delete(any())).thenReturn(future);
    }

    private CompletionStage<Response> whenGettingTheListOfBooks() {
        return resource.list();
    }

    private CompletionStage<Response> whenGettingTheBook(String id) {
        return resource.findById(id);
    }

    private CompletionStage<Response> whenAddingABook() {
        return resource.add(sampleBook);
    }

    private CompletionStage<Boolean> whenUpdatingTheBook() {
        Book updated = sampleBook;
        updated.setAuthor("Updated author");
        updated.setGenre(BookGenre.BIOGRAPHY);

        return resource.update(updated);
    }

    private CompletionStage<Boolean> whenDeletingTheBook() {
        return resource.delete(sampleBook.getIsbn());
    }

    private void thenBooksRetrievedAreCorrect(CompletionStage<Response> response) throws ExecutionException, InterruptedException {
        Response resp = response.toCompletableFuture().get();
        assertEquals(200, resp.getStatus());
        List<Book> retrieved = (List<Book>)resp.getEntity();
        assertEquals(bookList, retrieved);
    }

    private void thenTheBookRetrievedIsCorrect(CompletionStage<Response> response) throws ExecutionException, InterruptedException {
        Response resp = response.toCompletableFuture().get();
        assertEquals(200, resp.getStatus());
        Optional<Book> retrieved = ( Optional<Book>) resp.getEntity();
        assertEquals(sampleBook, retrieved.get());
    }

    private void thenTheErrorResponseIsCorrect(CompletionStage<Response> response) throws ExecutionException, InterruptedException {
        Response resp = response.toCompletableFuture().get();
        assertEquals(500, resp.getStatus());
    }

    private void thenTheEmptyResponseIsCorrect(CompletionStage<Response> response) throws ExecutionException, InterruptedException {
        Response resp = response.toCompletableFuture().get();
        assertEquals(204, resp.getStatus());
        assertNull(resp.getEntity());
    }

    private void thenBookIsAddedCorrectly(CompletionStage<Response> response) throws ExecutionException, InterruptedException {
        Response resp = response.toCompletableFuture().get();
        assertEquals(201, resp.getStatus());
        assertEquals(sampleBook.getIsbn(),  resp.getEntity().toString());
    }

    private void thenTheDatabaseIsUpdated(CompletionStage<Boolean> response) throws ExecutionException, InterruptedException {
        Boolean resp = response.toCompletableFuture().get();
        assertTrue(resp);
    }

    private void thenTheDatabaseIsNotUpdated(CompletionStage<Boolean> response) throws ExecutionException, InterruptedException {
        Boolean resp = response.toCompletableFuture().get();
        assertFalse(resp);
    }
}
