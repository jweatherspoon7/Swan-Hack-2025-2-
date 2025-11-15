package swanhack.User;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jackson Weatherspoon
 * @author Conner Houdek
 *
 */

@RestController
public class User2Controller {

    @Autowired
    User2Repository user2Repository;

    @GetMapping(path = "/users")
    List<User2> getAllUsers(){
        return user2Repository.findAll();
    }

    @GetMapping(path = "/users/id/{id}")
    User2 getUserById(@PathVariable int id){
        return user2Repository.findById(id);
    }

    @GetMapping(path = "/users/email/{email}")
    User2 getUserByEmail(@PathVariable("email") String email){
        return user2Repository.findByEmail(email);
    }

    @PostMapping(path = "/users")
    String createUser(@RequestBody User2 user2){
        if (user2 == null)
            return "user2 is null";
        user2Repository.save(user2);

        return "user2 created";
    }

    @PutMapping("/users/{id}")
    User2 updateUser(@PathVariable int id, @RequestBody User2 request){
        User2 user2 = user2Repository.findById(id);
        if(user2 == null)
            return null;

        // Save the new data over the old entry

        if(request.getEmail() != null) {
            user2.setEmail(request.getEmail());
        }

        if(request.getPassword() != null) {
            user2.setPassword(request.getPassword());
        }

        user2Repository.save(user2);
        return user2;
    }

    @DeleteMapping("/users/{id}")
    @Transactional
    User2 deleteUser(@PathVariable int id){
        User2 user2 = user2Repository.findById(id);
        if(user2 == null)
            return null;
        user2Repository.delete(user2);
        return user2;
    }
}