package swanhack.User2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jackson Weatherspoon
 *
 */

public interface User2Repository extends JpaRepository<User2, Long> {

    User2 findById(long id);

    User2 findByEmail(String email);

    @Transactional
    void deleteById(long id);
}