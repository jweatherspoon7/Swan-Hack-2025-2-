package swanhack.Bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import swanhack.Transaction.Transaction;
import swanhack.User2.User2;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>  {

    Bookmark findById(long id);

    List<Bookmark> findAllByUser2(User2 user2);

    @Transactional
    Bookmark deleteById(long id);
}
