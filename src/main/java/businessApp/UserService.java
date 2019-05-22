package businessApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Usermodel findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Usermodel saveUser(Usermodel usermodel, boolean pass) {
        if (pass) {
            usermodel.setPassword(bCryptPasswordEncoder.encode((usermodel.getPassword())));
        }

        userRepository.save(usermodel);
        return usermodel;
    }
}
