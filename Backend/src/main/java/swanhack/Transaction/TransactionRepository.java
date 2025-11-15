package swanhack.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import swanhack.User2.User2;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Transaction findById(long id);

    List<Transaction> findAllByUser2(User2 user2);

    @Transactional
    Transaction deleteById(long id);

}
