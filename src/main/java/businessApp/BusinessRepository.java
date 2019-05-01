package businessApp;

import org.springframework.data.repository.CrudRepository;

public interface BusinessRepository extends CrudRepository<User, Long> {

}
