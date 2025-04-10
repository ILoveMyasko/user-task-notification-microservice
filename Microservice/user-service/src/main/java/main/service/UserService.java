package main.service;
import main.model.User;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface UserService {

    User getUserById(long id);

    User createUser(User user);

    List<User> getAllUsers();

}