package swanhack.Bookmark;

import swanhack.User2.User2;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.*;

@Entity
public class Bookmark2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    @JsonBackReference //helps stop infinite recursion when using GET
    private User2 user2;

    //location stuff
    private String charityName;

    private int precentContribution;

    private double totalContribution;

    public Bookmark2(){}

    public Bookmark2(User2 user2, String charityName) {
        this.user2 = user2;
        this.charityName = charityName;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User2 getUser2() {
        return user2;
    }

    public void setUser2(User2 user2) {
        this.user2 = user2;
    }

    public String getCharityName() {
        return charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public int getPrecentContribution() {
        return precentContribution;
    }

    public void setPrecentContribution(int precentContribution) {
        this.precentContribution = precentContribution;
    }

    public double getTotalContribution() {
        return totalContribution;
    }

    public void setTotalContribution(double totalContribution) {
        this.totalContribution = totalContribution;
    }
}