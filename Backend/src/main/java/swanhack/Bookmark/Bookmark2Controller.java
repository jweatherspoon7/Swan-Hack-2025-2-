package swanhack.Bookmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;

import swanhack.User2.User2;
import swanhack.User2.User2Repository;

@RestController
public class Bookmark2Controller {

    @Autowired
    Bookmark2Repository bookmark2Repository;

    @Autowired
    User2Repository user2Repository;

    // Get all bookmarks
    @GetMapping("/bookmark")
    public List<Bookmark2> getBookmarks() {
        return bookmark2Repository.findAll();
    }

    // Get a specific bookmark by ID
    @GetMapping("/bookmark/{bookmarkId}")
    public Bookmark2 getBookmark(@PathVariable("bookmarkId") Long bookmarkId) {
        return bookmark2Repository.findById(bookmarkId).orElse(null);
    }

    // Get bookmarks by user ID
    @GetMapping("/bookmark/user/{userId}")
    public List<Bookmark2> getBookmarksByUser(@PathVariable("userId") int userId) {
        User2 user = user2Repository.findById(userId);
        if (user == null) {
            return null;
        }
        return bookmark2Repository.findAllByUser2(user);
    }

    /*
        JSON example for POST:

        {
            "userId": 1,
            "charityName": "Charity Example"
        }
    */
    @PostMapping("/bookmark")
    public String createBookmark(@RequestBody Map<String, Object> newBookmark) {
        int userId = (int) newBookmark.get("userId");
        User2 user = user2Repository.findById(userId);

        if (user == null) {
            return "User: " + userId + " does not exist";
        }

        Bookmark2 bookmark2 = new Bookmark2(user, (String) newBookmark.get("charityName"));
        bookmark2Repository.save(bookmark2);

        return "New bookmark2 created";
    }

    @PutMapping("/bookmark/{bookmarkId}")
    public String updateBookmark(@PathVariable("bookmarkId") Long bookmarkId,
                                 @RequestBody Map<String, Object> updatedBookmark) {
        Bookmark2 bookmark2 = bookmark2Repository.findById(bookmarkId).orElse(null);

        if (bookmark2 == null) {
            return "Bookmark2: " + bookmarkId + " does not exist";
        }

        bookmark2.setCharityName((String) updatedBookmark.get("charityName"));
        bookmark2.setPrecentContribution((int) updatedBookmark.get("precentContribution"));
        bookmark2.setTotalContribution((String) updatedBookmark.get("totalContribution"));

        bookmark2Repository.save(bookmark2);

        return "Update successful";
    }

    @Transactional
    @DeleteMapping("/bookmark/{bookmarkId}")
    public String deleteBookmark(@PathVariable("bookmarkId") Long bookmarkId) {
        Bookmark2 bookmark2 = bookmark2Repository.findById(bookmarkId).orElse(null);

        if (bookmark2 == null) {
            return "Bookmark2: " + bookmarkId + " does not exist";
        }

        bookmark2Repository.deleteById(bookmarkId);

        return "Deleted bookmark2";
    }
}
