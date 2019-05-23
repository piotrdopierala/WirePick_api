package pl.dopierala.wirepickapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.dopierala.wirepickapi.exceptions.definitions.UserLoginAlreadyExists;
import pl.dopierala.wirepickapi.exceptions.definitions.UserNotFoundException;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.repositories.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserByLogin(String login) throws UserNotFoundException {
        Optional<User> userFoundByLogin = userRepository.findByLogin(login);
        if (userFoundByLogin.isPresent()) {
            return userFoundByLogin.get();
        } else {
            throw new UserNotFoundException("User with login '" + login + "' not found.");
        }
    }

    public boolean isUserExists(String login) {
        Optional<User> userFoundByLogin = userRepository.findByLogin(login);
        return userFoundByLogin.isPresent();
    }

    public User findUserById(Long id) throws UserNotFoundException {
        Optional<User> userFoundById = userRepository.findById(id);
        if (userFoundById.isPresent()) {
            return userFoundById.get();
        } else {
            throw new UserNotFoundException("User with id '" + id + "' not found.");
        }
    }

    public void saveNewUser(User user) {
        if (isUserExists(user.getLogin())) {
            throw new UserLoginAlreadyExists("User with login " + user.getLogin() + " already exists.");
        } else {
            userRepository.save(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return findUserByLogin(username);
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("User name with login '" + username + "' not found.");
        }
    }
}
