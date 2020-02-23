package org.lvelez;

import com.google.common.collect.ImmutableList;
import io.quarkus.mongodb.ReactiveMongoClient;
import io.quarkus.mongodb.ReactiveMongoCollection;
import io.quarkus.mongodb.ReactiveMongoDatabase;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lvelez.book.Book;
import org.lvelez.book.BookGenre;
import org.lvelez.book.BookRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookRepositoryTest {
    @Mock
    ReactiveMongoClient mongoClient;

    @Mock
    ReactiveMongoDatabase mongoDatabase;

    @Mock
    ReactiveMongoCollection<Book> mongoCollection;

    @InjectMocks
    BookRepository repository = new BookRepository();

    List<Book> bookList =
            ImmutableList.of(new Book("isbn", "name", BookGenre.ACTION, "author", "description", "language", 120),
                    new Book("isbn2", "book 2", BookGenre.ROMANCE, "author", "description 2", "spanish", 1100),
                    new Book("isbn3", "book 3", BookGenre.HISTORICAL, "other author", "description 3", "english", 321),
                    new Book("isbn4", "book 4", BookGenre.THRILLER, "author", "description 4", "esperanto", 135));

    @Test
    public void shouldListAllBooks() throws ExecutionException, InterruptedException {
        givenAConnectiontoMongoDb();
        CompletionStage<List<Book>> list =  whenListingBooks();
        thenConnectionToDatabaseAndCollectionIsCorrect();
        andBooksAreRetrieved(list);
    }

    private void givenAConnectiontoMongoDb() {
        PublisherBuilder<Book> books = ReactiveStreams.fromIterable(bookList);

        when(mongoCollection.find()).thenReturn(books);
        when(mongoDatabase.getCollection(anyString(), eq(Book.class))).thenReturn(mongoCollection);
        when(mongoClient.getDatabase(anyString())).thenReturn(mongoDatabase);
    }

    private CompletionStage<List<Book>> whenListingBooks() {
        return repository.list();
    }

    private void thenConnectionToDatabaseAndCollectionIsCorrect() {
        verify(mongoClient).getDatabase("book_catalog");
        verify(mongoDatabase).getCollection("books", Book.class);
    }

    private void andBooksAreRetrieved(CompletionStage<List<Book>> list) throws ExecutionException, InterruptedException {
        assertEquals(list.toCompletableFuture().get(), bookList);

    }
}
