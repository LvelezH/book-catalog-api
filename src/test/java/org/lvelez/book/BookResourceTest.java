package org.lvelez.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookResourceTest {
    @Mock
    BookRepository repository;

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

    private void givenABookInTheDatabase() {
        CompletableFuture<Optional<Book>> future = new CompletableFuture<>();
        future.complete(Optional.of(sampleBook));

        when(repository.findById(sampleBook.getIsbn())).thenReturn(future);
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

    private CompletionStage<Response> whenGettingTheListOfBooks() {
        return resource.list();
    }

    private CompletionStage<Response> whenGettingTheBook(String id) {
        return resource.findById(id);
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

}
