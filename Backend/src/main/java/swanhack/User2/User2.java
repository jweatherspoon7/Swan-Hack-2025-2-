package swanhack.User2;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import swanhack.Transaction.Transaction;

import java.util.Set;


/**
 *
 * @author Jackson Weatherspoon
 *
 */

@Entity
public class User2 {
    /*16:50:18	DROP TABLE `DB309`.`laptop`	Error Code: 1451. Cannot delete or update a parent row: a foreign key constraint fails	0.031 sec
     * The annotation @ID marks the field below as the primary key for the table created by springboot
     * The @GeneratedValue generates a value if not already present, The strategy in this case is to start from 1 and increment for each table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Transaction> transactionSet;

    private String email;

    private String password;



    public User2(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User2() {}

    // =============================== Getters and Setters for each field ================================== //

    public long getId(){
    return id;
}

    public void setId(int id){
    this.id = id;
}

    public String getEmail(){
    return email;
}

    public void setEmail(String email){
    this.email = email;
}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Transaction> getTransactionSet() {
        return transactionSet;
    }

    public void setTransactionSet(Set<Transaction> transactionSet) {
        this.transactionSet = transactionSet;
    }
}
