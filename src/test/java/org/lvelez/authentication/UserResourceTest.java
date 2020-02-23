package org.lvelez.authentication;

import authentication.User;
import authentication.UserRepository;
import authentication.UserResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class UserResourceTest {
    @Mock
    UserRepository repository;

    @InjectMocks
    UserResource resource;

    private static final User user = new User("username", "password");
    private static final User userRetrieved = new User("username1", "password1");
    private static final User userInvalid = new User("username1", "whatever");
    private static final User notExistingUser = new User("nope", "whatever");

    @Test
    public void shouldReturnTrueForaValidLogin() throws ExecutionException, InterruptedException {
        givenUsersInTheDatabase();
        CompletionStage<Boolean> response =  whenTheUserLogsIn();
        thenTheLoginIsCorrect(response);
    }

    @Test
    public void shouldReturnFalseForInvalidLogin() throws ExecutionException, InterruptedException {
        givenUsersInTheDatabase();
        CompletionStage<Boolean> response =  whenTheUserLogsInWithInvalidgUser();
        thenTheLoginIsIncorrect(response);
    }

    @Test
    public void shouldReturnFalseForNotExistingUser() throws ExecutionException, InterruptedException {
        givenUsersInTheDatabase();
        CompletionStage<Boolean> response =  whenTheUserLogsInWithNotExistingUser();
        thenTheLoginIsIncorrect(response);
    }

    private void givenUsersInTheDatabase() {
        CompletableFuture<Optional<User>> future = new CompletableFuture<>();
        CompletableFuture<Optional<User>> invalid = new CompletableFuture<>();
        CompletableFuture<Optional<User>> notExisting = new CompletableFuture<>();

        future.complete(Optional.of(user));
        invalid.complete(Optional.of(userInvalid));
        notExisting.complete(Optional.empty());

        lenient().when(repository.login(user)).thenReturn(future);
        lenient().when(repository.login(userRetrieved)).thenReturn(invalid);
        lenient().when(repository.login(notExistingUser)).thenReturn(notExisting);
    }

    private CompletionStage<Boolean> whenTheUserLogsIn() {
        return resource.login(user);
    }

    private CompletionStage<Boolean> whenTheUserLogsInWithInvalidgUser() {
        return resource.login(userRetrieved);
    }

    private CompletionStage<Boolean> whenTheUserLogsInWithNotExistingUser() {
        return resource.login(notExistingUser);
    }

    private void thenTheLoginIsCorrect(CompletionStage<Boolean> response) throws ExecutionException, InterruptedException {
        Boolean resp = response.toCompletableFuture().get();
        assertTrue(resp);
    }

    private void thenTheLoginIsIncorrect(CompletionStage<Boolean> response) throws ExecutionException, InterruptedException {
        Boolean resp = response.toCompletableFuture().get();
        assertFalse(resp);
    }
}
