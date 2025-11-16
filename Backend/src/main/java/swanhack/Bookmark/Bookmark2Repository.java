package swanhack.Bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import swanhack.User2.User2;

import java.util.List;

public interface Bookmark2Repository extends JpaRepository<Bookmark2, Long>  {

    Bookmark2 findById(long id);

    List<Bookmark2> findAllByUser2(User2 user2);

    @Transactional
    Bookmark2 deleteById(long id);
}
