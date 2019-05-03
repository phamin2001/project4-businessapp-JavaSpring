package businessApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserService userService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/login")
    public String[] login(@RequestBody User login, HttpSession session) throws IOException {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User user = userRepository.findByUsername(login.getUsername());
        if (user == null) {
            throw new IOException("Invalid Credentials");
        }
        boolean valid = bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword());
        if (valid) {
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userId", user.getId());
            session.setAttribute("logged", true);
            return new String[] {user.getUsername(), String.valueOf(user.getId())};
        } else {
            throw new IOException("Invalid Credentials");
        }
    }

//    @GetMapping("/users")
//    public Iterable<User> getusers() {
//        return userRepository.findAll();
//    }

    @GetMapping("/users/{userId}")
    public User showUser(@PathVariable Long userId, HttpSession session) throws Exception {
//        System.out.println(session.getAttribute("username") + " : session");

        Optional<User> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()) {
            User result = foundUser.get();
            return result;
        } else {
            throw new Exception("No User Found By This Id!!!");
        }
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user, HttpSession session) {
        User createdUser = userService.saveUser(user);
        session.setAttribute("username", user.getUsername());
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
