package businessApp;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Usermodel, Long> {
    Usermodel findByUsername(String username);

}