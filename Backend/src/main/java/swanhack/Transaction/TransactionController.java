package swanhack.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;

import swanhack.User2.User2;
import swanhack.User2.User2Repository;

@RestController
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    User2Repository user2Repository;

    @GetMapping("/transaction")
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/transaction/{transactionId}")
    public Transaction getTransaction(@PathVariable("transactionId") int transactionId) {
        return transactionRepository.findById(transactionId);
    }

    //get by using user Id
    @GetMapping("/transaction/user/{userId}")
    public List<Transaction> getTransactionByUser(@PathVariable("userId") int userId){
        User2 user = user2Repository.findById(userId);

        if (user == null) {
            return null;
        }
        return transactionRepository.findAllByUser2(user);
    }

    /*
        Json example

        {
            "userId": " ",
            "transactionName": " ",
            "total": ,
            "amountDonated": ,
            "month": ,
            "day": ,
            "year":
        }

    */
    @PostMapping("/transaction")
    public String createTransaction(@RequestBody Map<String, Object> newTransaction){
        long userId = (long) newTransaction.get("userId");
        User2 user = user2Repository.findById(userId);

        if(user == null){
            return "User: " + userId + " does not exist";
        }

        Transaction transaction = new Transaction( user, (String) newTransaction.get("transactionName"),
                (double) newTransaction.get("total"), (double) newTransaction.get("amountDonated"),
                (int) newTransaction.get("month"), (int) newTransaction.get("day"), (int) newTransaction.get("year"));

        transactionRepository.save(transaction);

        return "New transaction created";
    }

    @PutMapping("/transaction/{transactionId}")
    public String updateTransaction(@PathVariable("transactionId") int transactionId,
                                     @RequestBody Map<String, Object> newTransaction){

        Transaction transaction = transactionRepository.findById(transactionId);

        if (transaction == null) {
            return "Transaction: " + transactionId + " does not exist";
        }

        transaction.setTransactionName((String) newTransaction.get("transactionName"));
        transaction.setTotal((double) newTransaction.get("total"));
        transaction.setAmountDonated((double) newTransaction.get("amountDonated"));
        transaction.setMonth((int) newTransaction.get("month"));
        transaction.setDay((int) newTransaction.get("day"));
        transaction.setYear((int) newTransaction.get("year"));

        transactionRepository.save(transaction);

        return "Update sucessful";
    }

    @Transactional
    @DeleteMapping("/transaction/{transactionId}")
    public String deleteNotification(@PathVariable("transactionId") int transactionId){
        Transaction transaction = transactionRepository.findById(transactionId);

        if (transaction == null) {
            return "Notification: " + transactionId + " does not exist";
        }

        transactionRepository.deleteById(transactionId);

        return "deleted notification";
    }
}
