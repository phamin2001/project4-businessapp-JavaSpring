package businessApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @RequestMapping(value="*", method=RequestMethod.OPTIONS)
    public HttpStatus optionsRoute(){
        return HttpStatus.OK;
    }

    @GetMapping("/")
    public String hello() {
        return "hello out there";
    }

    @PostMapping("/users/login")
    public HashMap<String, String> login(@RequestBody Usermodel login, HttpSession session) throws IOException {

        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Usermodel usermodel = userRepository.findByUsername(login.getUsername());

        if (usermodel == null) {
            throw new IOException("Invalid Credentials!!");
        }

        boolean valid = bCryptPasswordEncoder.matches(login.getPassword(), usermodel.getPassword());

        if (valid) {
            session.setAttribute("username", usermodel.getUsername());
            session.setAttribute("userId", usermodel.getId());
            session.setAttribute("logged", true);

            HashMap<String, String> userInfo = new HashMap<>();
            userInfo.put("username", String.valueOf(session.getAttribute("username")));
            userInfo.put("userId", String.valueOf(session.getAttribute("userId")));
            userInfo.put("logged", String.valueOf(session.getAttribute("logged")));

            return userInfo;

        } else {
            throw new IOException("Invalid Credentials");
        }
    }

    @GetMapping("/users/{userId}")
    public Object[] showUser(@PathVariable Long userId, HttpSession session) throws Exception {
        Optional<Usermodel> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()) {
            Usermodel result = foundUser.get();
            if(result.getId() == session.getAttribute("userId")) {
                return new Object[] {result.getId(), result.getUsername()};
            } else {
                throw new Exception("You didn't login!!");
            }
        } else {
            throw new Exception("No Usermodel Found By This Id!!!");
        }
    }

    @PostMapping("/users/register")
    public HashMap<String, Object> createUser(@RequestBody Usermodel usermodel, HttpSession session) {
        HashMap<String, Object> newUser = new HashMap<>();

        // check if usermodel exist
        if (userRepository.findByUsername(usermodel.getUsername()) == null) {
            Usermodel createdUsermodel = userService.saveUser(usermodel, true);
            session.setAttribute("username", createdUsermodel.getUsername());
            session.setAttribute("userId", createdUsermodel.getId());
            session.setAttribute("logged", true);

            newUser.put("username", session.getAttribute("username"));
            newUser.put("userId",   session.getAttribute("userId"));
            newUser.put("logged", session.getAttribute("logged"));

            return newUser;
        } else {
            System.out.println("Usermodel Exists!");
            newUser.put("logged", false);
            return newUser;
        }
    }

    @PutMapping("/users/{userId}")
    public HashMap<String, String> updateUser(@RequestBody Usermodel usermodel, @PathVariable Long userId, HttpSession session) throws Exception {
        Optional<Usermodel> foundUser = userRepository.findById(userId);
        boolean flag = false;

        if(foundUser.isPresent()) {
            Usermodel updatedUsermodel = foundUser.get();

            if(updatedUsermodel.getId() == session.getAttribute("userId")) {

                if (!usermodel.getUsername().isEmpty()) {
                    updatedUsermodel.setUsername(usermodel.getUsername());
                }

                if (!usermodel.getPassword().isEmpty()) {
                    updatedUsermodel.setPassword(usermodel.getPassword());
                    flag = !flag;
                }

                updatedUsermodel = userService.saveUser(updatedUsermodel, flag);
                session.setAttribute("username", updatedUsermodel.getUsername());

                HashMap<String, String> returnUpdatedUser = new HashMap<>();
                returnUpdatedUser.put("username", updatedUsermodel.getUsername());
                returnUpdatedUser.put("userId", String.valueOf(updatedUsermodel.getId()));
                returnUpdatedUser.put("edited", String.valueOf(true));

               return returnUpdatedUser;
            } else {
                throw new Exception("You didn't logged in!!!! OR this username is already taken.");
            }
        } else {
            throw new Exception("No Usermodel Found By This Id!!!");
        }
    }

    @DeleteMapping("/users/{userId}")
    public HashMap<String, String> deleteUser(@PathVariable Long userId, HttpSession session) throws Exception {

        Optional<Usermodel> foundUser = userRepository.findById(userId);
        if (foundUser.isPresent()) {

            if (foundUser.get().getId() == session.getAttribute("userId")) {
                Iterable<Business> foundAllUserBusinesses = businessRepository.findAll();
                for (Business business : foundAllUserBusinesses) {
                    if (business.getUsermodel().getId() == userId) {
                        businessRepository.deleteById(business.getId());
                    }
                }

                userRepository.deleteById(userId);

                HashMap<String, String> result = new HashMap<>();
                result.put("status", "OK");
                result.put("message", "Successfully Delete Usermodel By Id Of: " + userId);

                return result;
            } else {
                throw new Exception("You didn't logged in!!!!");
            }
        } else {
            throw new Exception("No Usermodel Found By This Id");
        }
    }

    @PostMapping("/auth/logout")
    public HashMap<String, String> logoutUser( HttpServletRequest request){
        HttpSession session= request.getSession(false);
        SecurityContextHolder.clearContext();
        session= request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("status", "200");
        result.put("data", "logout successful");
        return result;
    }
}