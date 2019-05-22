package businessApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
public class BusinessController {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;



    @GetMapping("/users/{userId}/businesses")
    public List<Map<String, String>> getBusinesses(@PathVariable Long userId, HttpSession session) throws Exception{

        List<Map<String, String>> businessesList = new ArrayList<Map<String, String>>();

        if(session.getAttribute("userId") == userId) {
            Iterable<Business> foundAllUserBusinesses = businessRepository.findAll();
            int i = 0;
            for (Business business : foundAllUserBusinesses) {
                HashMap<String, String> businessMap = new HashMap<>();

                if (business.getUsermodel().getId() == userId) {
                    businessMap.put("name", business.getName());
                    businessMap.put("location", business.getLocation());
                    businessMap.put("id", String.valueOf(business.getId()));

                    businessesList.add(i, businessMap);
                    i++;
                }
            }
            return businessesList;
        } else {
            throw new Exception("Not a valid user!!");
        }
    }

    @PostMapping("/users/{userId}/businesses")
    public HashMap<String, String> createBusiness(@RequestBody Business business, @PathVariable Long userId, HttpSession session) throws Exception {
        HashMap<String, String> newBusiness = new HashMap<>();

        if (session.getAttribute("userId") == userId) {
            String currentUserUsername = session.getAttribute("username").toString();
            Usermodel currentUsermodel = userRepository.findByUsername(currentUserUsername);

            Set<Business> allUserBusinesses = currentUsermodel.getBusinesses();
            for (Business userBusiness : allUserBusinesses) {
                if( (userBusiness.getName().equals(business.getName())) &&
                        (userBusiness.getLocation().equals(business.getLocation()))) {
                    throw new Exception("Usermodel already has this business.");
                }
            }

            business.setUsermodel(currentUsermodel);
            Business createdBusiness = businessRepository.save(business);

            newBusiness.put("businessId", String.valueOf(createdBusiness.getId()));
            newBusiness.put("name", createdBusiness.getName());
            newBusiness.put("location", createdBusiness.getLocation());

            return newBusiness;
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
    public HashMap<String, String> updateBusiness(@RequestBody Business business, @PathVariable Long businessId, @PathVariable Long userId, HttpSession session) throws Exception {
        HashMap<String, String> returnUpdatedBusiness = new HashMap<>();
        if (session.getAttribute("userId") == userId) {

            Optional<Business> foundBusiness = businessRepository.findById(businessId);
            if (foundBusiness.isPresent()) {
                Business updatedBusiness = foundBusiness.get();
                updatedBusiness.setName(business.getName());
                updatedBusiness.setLocation(business.getLocation());
                businessRepository.save(updatedBusiness);

                returnUpdatedBusiness.put("id", String.valueOf(updatedBusiness.getId()));
                returnUpdatedBusiness.put("name", updatedBusiness.getName());
                returnUpdatedBusiness.put("location", updatedBusiness.getLocation());

                return returnUpdatedBusiness;
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
