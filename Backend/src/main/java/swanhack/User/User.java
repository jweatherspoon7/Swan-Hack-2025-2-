package swanhack.User;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.*;

import java.util.Set;


/**
 *
 * @author Jackson Weatherspoon
 *
 */

@Entity
public class User{
    /*16:50:18	DROP TABLE `DB309`.`laptop`	Error Code: 1451. Cannot delete or update a parent row: a foreign key constraint fails	0.031 sec
     * The annotation @ID marks the field below as the primary key for the table created by springboot
     * The @GeneratedValue generates a value if not already present, The strategy in this case is to start from 1 and increment for each table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String email;
    private String password;



    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User() {}

    // =============================== Getters and Setters for each field ================================== //

    public int getId(){
    return id;
}

    public void setId(int id){
    this.id = id;
}

    public String getUsername(){
    return username;
}

    public void setUsername(String username){
    this.username = username;
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
}
