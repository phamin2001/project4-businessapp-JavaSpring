package businessApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @GetMapping("/users")
    public Iterable<User> getusers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{userId}")
    public User showUser(@PathVariable Long userId) throws Exception {
        Optional<User> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()) {
            User result = foundUser.get();
            return result;
        } else {
            throw new Exception("No User Found By This Id!!!");
        }
    }

    // TODO: saveUser(user)
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        User createdUser = userRepository.save(user);
        return createdUser;
    }

    // TODO: hash password
    @PutMapping("users/{userId}")
    public User updateUser(@RequestBody User user, @PathVariable Long userId) throws Exception {
        Optional<User> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()) {
            User updatedUser = foundUser.get();
            updatedUser.setUsername(user.getUsername());
            updatedUser.setPassword(user.getPassword());
            return userRepository.save(updatedUser);
        } else {
            throw new Exception("No User Found By This Id!!!");
        }
    }

    @DeleteMapping("users/{userId}")
    public String deleteUser(@PathVariable Long userId) throws Exception {
        Iterable<Business> foundAllUserBusinesses = businessRepository.findAll();
        for (Business business : foundAllUserBusinesses) {
            if (business.getUser().getId() == userId) {
                businessRepository.deleteById(business.getId());
            }
        }

        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isPresent()) {
            userRepository.deleteById(userId);
            return "Successfully Delete User By Id Of: " + userId;
        } else {
            throw new Exception("No User Found By This Id");
        }

    }

}
