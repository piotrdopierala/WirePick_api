package pl.dopierala.wirepickapi.service;


import javassist.tools.rmi.Sample;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.dopierala.wirepickapi.SampleStock;
import pl.dopierala.wirepickapi.SampleUsers;
import pl.dopierala.wirepickapi.exceptions.definitions.UserLoginAlreadyExists;
import pl.dopierala.wirepickapi.exceptions.definitions.UserNotFoundException;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.repositories.user.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepositoryMock;

    @InjectMocks
    UserService userService;

    @Test
    public void Should_findAllUsers_ReturnUsers(){
        final List<User> usersSampleList = Arrays.asList(SampleUsers.u1, SampleUsers.u2);
        when(userRepositoryMock.findAll()).thenReturn(usersSampleList);

        List<User> allUsersFoundByService = userService.findAllUsers();

        Assert.assertEquals(allUsersFoundByService,usersSampleList);
    }

    @Test
    public void Should_findByLogin_ReturnUser(){
        final User sampleUser = SampleUsers.u2;
        when(userRepositoryMock.findByLogin(sampleUser.getLogin())).thenReturn(Optional.of(sampleUser));

        User userByLogin = userService.findUserByLogin(sampleUser.getLogin());

        Assert.assertEquals(userByLogin,sampleUser);
    }

    @Test
    public void Should_findUserById_ReturnUser(){
        final User sampleUser = SampleUsers.u2;
        when(userRepositoryMock.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        User userById = userService.findUserById(sampleUser.getId());

        Assert.assertEquals(userById,sampleUser);
    }

    @Test
    public void Should_isUserExists_ReturnTrueWhenFound(){
        final User sampleUser = SampleUsers.u2;
        when(userRepositoryMock.findByLogin(sampleUser.getLogin())).thenReturn(Optional.of(sampleUser));

        boolean userExists = userService.isUserExists(sampleUser.getLogin());

        Assert.assertTrue(userExists);
    }

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