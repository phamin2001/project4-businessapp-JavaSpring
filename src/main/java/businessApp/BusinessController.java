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

    @PostMapping("/users/{userId}/businesses")
    public Object[] createBusiness(@RequestBody Business business, @PathVariable Long userId, HttpSession session) throws Exception {

        if (session.getAttribute("userId") == userId) {
            String currentUserUsername = session.getAttribute("username").toString();
            User currentUser = userRepository.findByUsername(currentUserUsername);

            business.setUser(currentUser);
            Business createdBusiness = businessRepository.save(business);
            return new Object[] {createdBusiness.getId(), createdBusiness.getName(), createdBusiness.getLocation()};
        } else {
            throw new Exception("You didn't logged in!!");
        }
    }

    @GetMapping("/users/{uerId}/businesses/{businessId}")
    public Object[] showOne(@PathVariable Long userId, @PathVariable Long businessId, HttpSession session) throws Exception {
        if (session.getAttribute("userId") == userId) {
            Business findOne = businessRepository.findById(businessId).get();
            return new Object[] {findOne.getId(), findOne.getName(), findOne.getLocation()};
        } else {
            throw new Exception("You didn't logged in!!");
        }
    }

    @PutMapping("/users/{userId}/businesses/{businessId}")
    public Object[] updateBusiness(@RequestBody Business business, @PathVariable Long businessId, @PathVariable Long userId, HttpSession session) throws Exception {

        if (session.getAttribute("userId") == userId) {

            Optional<Business> foundBusiness = businessRepository.findById(businessId);
            if (foundBusiness.isPresent()) {
                Business updatedBusiness = foundBusiness.get();
                updatedBusiness.setName(business.getName());
                updatedBusiness.setLocation(business.getLocation());
                businessRepository.save(updatedBusiness);
                return new Object[] {updatedBusiness.getId(), updatedBusiness.getName(), updatedBusiness.getLocation()};
            } else {
                throw new Exception("No Business By This Id!!!");
            }
        } else {
            throw new Exception("You didn't log in!!!");
        }
    }

    @DeleteMapping("/users/{userId}/businesses/{businessId}")
    public String deleteUser(@PathVariable Long businessId, @PathVariable Long userId, HttpSession session) throws Exception {
        if (session.getAttribute("userId") == userId) {

            Optional<Business> foundBusiness = businessRepository.findById(businessId);
            if (foundBusiness.isPresent()) {
                businessRepository.deleteById(businessId);
                return "Successfully Deleted Business By Id Of: " + businessId;
            } else {
                throw new Exception("No Business By This Id.");
            }
        } else {
            throw new Exception("You didn't log in!!!");
        }
    }
}
