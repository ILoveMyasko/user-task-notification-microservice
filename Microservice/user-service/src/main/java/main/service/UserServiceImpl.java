package main.service;

import main.exceptions.UserAlreadyExistsException;
import main.exceptions.UserNotFoundException;
import main.model.User;
import main.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        // Проверяем существование пользователя по ID
        if (userRepository.existsById(user.getUserId())) {
            throw new UserAlreadyExistsException(user.getUserId());
        }
        return userRepository.save(user);
    }

    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}