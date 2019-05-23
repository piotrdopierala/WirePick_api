package pl.dopierala.wirepickapi.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.dopierala.wirepickapi.exceptions.definitions.UserLoginAlreadyExists;
import pl.dopierala.wirepickapi.exceptions.definitions.UserNotFoundException;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.repositories.user.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepositoryMock;

    @InjectMocks
    UserService userService;

    //TODO finish user service tests

    @Test(expected = UserNotFoundException.class)
    public void Should_findUserByLogin_ThrowException_WhenUserNotFound() {
        final long userId = 1L;

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.empty());

        userService.findUserById(userId);
    }

    @Test(expected = UserLoginAlreadyExists.class)
    public void Should_saveUser_ThrowException_WhenLoginAlreadyExists(){
        final User sampleUser = User.builder()
                .withFirstName("Piotr")
                .withLogin("Piotrek")
                .build();

        when(userRepositoryMock.findByLogin(sampleUser.getLogin())).thenReturn(Optional.of(sampleUser));

        userService.saveNewUser(sampleUser);
    }
}