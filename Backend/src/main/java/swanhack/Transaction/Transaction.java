package swanhack.Transaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import swanhack.User2.User2;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference //helps stop infinite recursion when
    private User2 user;

    private String transactionName;

    private double total;

    private double amountDonanted;

    public Transaction(){}

    public Transaction(User2 user, String transactionName, double total, double amountDonanted) {
        this.user = user;
        this.transactionName = transactionName;
        this.total = total;
        this.amountDonanted = amountDonanted;
    }

    public User2 getUser() {
        return user;
    }

    public void setUser(User2 user) {
        this.user = user;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getAmountDonanted() {
        return amountDonanted;
    }

    public void setAmountDonanted(double amountDonanted) {
        this.amountDonanted = amountDonanted;
    }
}
