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
    @JoinColumn(name = "user2_id", nullable = false)
    @JsonBackReference //helps stop infinite recursion when
    private User2 user2;

    private String transactionName;

    private double total;

    private double amountDonated;

    //DATE
    private int month;
    private int day;
    private int year;

    public Transaction(){}

    public Transaction(User2 user2, String transactionName, double total, double amountDonated, int month, int day, int year) {
        this.user2 = user2;
        this.transactionName = transactionName;
        this.total = total;
        this.amountDonated = amountDonated;
        this.month = month;
        this.day = day;
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User2 getUser2() {
        return user2;
    }

    public void setUser2(User2 user) {
        this.user2 = user;
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

    public double getAmountDonated() {
        return amountDonated;
    }

    public void setAmountDonated(double amountDonated) {
        this.amountDonated = amountDonated;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "year=" + year +
                ",day=" + day +
                ",month=" + month +
                ",amountDonated=" + amountDonated +
                ",total=" + total +
                ",transactionName=" + transactionName;
    }
}
