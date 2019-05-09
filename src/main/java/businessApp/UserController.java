package businessApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
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

    @PostMapping("/users/login")
    public HashMap<String, Object> login(@RequestBody User login, HttpSession session) throws IOException {
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

            HashMap<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", session.getAttribute("username"));
            userInfo.put("userId",   session.getAttribute("userId"));
            userInfo.put("logged",   session.getAttribute("logged"));

            return userInfo;

        } else {
            throw new IOException("Invalid Credentials");
        }
    }

    @GetMapping("/users/{userId}")
    public Object[] showUser(@PathVariable Long userId, HttpSession session) throws Exception {
        Optional<User> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()) {
            User result = foundUser.get();
            if(result.getId() == session.getAttribute("userId")) {
                return new Object[] {result.getId(), result.getUsername()};
            } else {
                throw new Exception("You didn't login!!");
            }
        } else {
            throw new Exception("No User Found By This Id!!!");
        }
    }

    @PostMapping("/users")
    public Object[] createUser(@RequestBody User user, HttpSession session) {
        User createdUser = userService.saveUser(user);
        session.setAttribute("username", createdUser.getUsername());
        session.setAttribute("userId", createdUser.getId());
        session.setAttribute("logged", true);
        return new Object[] {session.getAttribute("username"),
                             session.getAttribute("userId"),
                             session.getAttribute("logged")};
    }

    @PutMapping("/users/{userId}")
    public Object[] updateUser(@RequestBody User user, @PathVariable Long userId, HttpSession session) throws Exception {
        Optional<User> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()) {
            User updatedUser = foundUser.get();
            if(updatedUser.getId() == session.getAttribute("userId")) {
               updatedUser = userService.saveUser(user);
               session.setAttribute("username", updatedUser.getUsername());
               return new Object[] {session.getAttribute("username"),
                                    session.getAttribute("userId")};
            } else {
                throw new Exception("You didn't logged in!!!!");
            }
        } else {
            throw new Exception("No User Found By This Id!!!");
        }
    }

    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable Long userId, HttpSession session) throws Exception {

        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isPresent()) {

            if (foundUser.get().getId() == session.getAttribute("userId")) {
                Iterable<Business> foundAllUserBusinesses = businessRepository.findAll();
                for (Business business : foundAllUserBusinesses) {
                    if (business.getUser().getId() == userId) {
                        businessRepository.deleteById(business.getId());
                    }
                }

                userRepository.deleteById(userId);
                return "Successfully Delete User By Id Of: " + userId;
            } else {
                throw new Exception("You didn't logged in!!!!");
            }
        } else {
            throw new Exception("No User Found By This Id");
        }
    }



    @PostMapping("/login/logout")
    public String logoutDo( HttpServletRequest request){
        System.out.println("here");

        HttpSession session= request.getSession(false);
        SecurityContextHolder.clearContext();
        session= request.getSession(false);
        if(session != null) {
            session.invalidate();
        }

        return "logout";
    }








}