package businessApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class BusinessController {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/businesses")
    public Iterable<Business> getBusinesses() {
        return businessRepository.findAll();
    }

    @GetMapping("businesses/{businessId}")
    public Business showOne(@PathVariable Long businessId) {
        Business findOne = businessRepository.findById(businessId).get();
        return findOne;
    }

    // TODO: PostMapping


    // TODO: Test if updating biusiness appky to related Uer
    @PutMapping("/businesses/{businessId}")
    public Business updateBusiness(@RequestBody Business business, @PathVariable Long businessId) throws Exception {
        Optional<Business> foundBusiness = businessRepository.findById(businessId);
        if (foundBusiness.isPresent()) {
            Business updatedBusiness = foundBusiness.get();
            updatedBusiness.setName(business.getName());
            updatedBusiness.setLocation(business.getLocation());
            return businessRepository.save(updatedBusiness);
        } else {
            throw new Exception("No Business By This Id!!!");
        }
    }

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
