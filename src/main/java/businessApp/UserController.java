package businessApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public Iterable<User> getusers() {
        return userRepository.findAll();
    }

    @PostMapping("/posts")
    public User createPost(@RequestBody User user) {
        User createdUser = userRepository.save(user);
        return createdUser;
    }

}
