package businessApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Optional;

@RestController
public class BusinessController {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


  
    @GetMapping("/users/{userId}/businesses")
    public Object getBusinesses(@PathVariable Long userId, HttpSession session) throws Exception{

        ArrayList<Object> businessesList = new ArrayList<>();

        if(session.getAttribute("userId") == userId) {
            Iterable<Business> foundAllUserBusinesses = businessRepository.findAll();
            for (Business business : foundAllUserBusinesses) {
                if (business.getUser().getId() == userId) {
                    businessesList.add(new String[]{business.getName(), business.getLocation(), String.valueOf(business.getId())});
                }
            }
            return businessesList;
        } else {
            throw new Exception("Not a valid user!!");
        }
    }

    // TODO: do not return password
    @PostMapping("/users/{userId}/businesses")
    public Business createBusiness(@RequestBody Business business, @PathVariable Long userId, HttpSession session) throws Exception {

        if (session.getAttribute("userId") == userId) {
            String currentUserUsername = session.getAttribute("username").toString();
            User currentUser = userRepository.findByUsername(currentUserUsername);

            business.setUser(currentUser);
            Business createdBusiness = businessRepository.save(business);
            return createdBusiness;
        } else {
            throw new Exception("You didn't logged in!!");
        }
    }

    @GetMapping("/users/{uerId}/businesses/{businessId}")
    public Business showOne(@PathVariable Long userId, @PathVariable Long businessId, HttpSession session) throws Exception {
        if (session.getAttribute("userId") == userId) {
            Business findOne = businessRepository.findById(businessId).get();
            return findOne;
        } else {
            throw new Exception("You didn't logged in!!");
        }
    }

    // TODO: do not return user password
    @PutMapping("/users/{userId}/businesses/{businessId}")
    public Business updateBusiness(@RequestBody Business business, @PathVariable Long businessId, @PathVariable Long userId, HttpSession session) throws Exception {

        if (session.getAttribute("userId") == userId) {

            Optional<Business> foundBusiness = businessRepository.findById(businessId);
            if (foundBusiness.isPresent()) {
                Business updatedBusiness = foundBusiness.get();
                updatedBusiness.setName(business.getName());
                updatedBusiness.setLocation(business.getLocation());
                return businessRepository.save(updatedBusiness);
            } else {
                throw new Exception("No Business By This Id!!!");
            }
        } else {
            throw new Exception("You didn't log in!!!");
        }
    }

    // TODO: test
    @DeleteMapping("/businesses/{businessId}")
    public String deleteUser(@PathVariable Long businessId) throws Exception {
        Optional<Business> foundBusiness = businessRepository.findById(businessId);
        if (foundBusiness.isPresent()) {
            businessRepository.deleteById(businessId);
            return "Successfully Deleted Business By Id Of: " + businessId;
        } else {
            throw new Exception("No Business By This Id.");
        }
    }
}
