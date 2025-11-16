package swanhack.Bookmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;

import swanhack.User2.User2;
import swanhack.User2.User2Repository;

@RestController
public class BookmarkController {

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Autowired
    User2Repository user2Repository;

    // Get all bookmarks
    @GetMapping("/bookmark")
    public List<Bookmark> getBookmarks() {
        return bookmarkRepository.findAll();
    }

    // Get a specific bookmark by ID
    @GetMapping("/bookmark/{bookmarkId}")
    public Bookmark getBookmark(@PathVariable("bookmarkId") Long bookmarkId) {
        return bookmarkRepository.findById(bookmarkId).orElse(null);
    }

    // Get bookmarks by user ID
    @GetMapping("/bookmark/user/{userId}")
    public List<Bookmark> getBookmarksByUser(@PathVariable("userId") int userId) {
        User2 user = user2Repository.findById(userId);
        if (user == null) {
            return null;
        }
        return bookmarkRepository.findAllByUser2(user);
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

        Bookmark bookmark = new Bookmark(user, (String) newBookmark.get("charityName"));
        bookmarkRepository.save(bookmark);

        return "New bookmark created";
    }

    @PutMapping("/bookmark/{bookmarkId}")
    public String updateBookmark(@PathVariable("bookmarkId") Long bookmarkId,
                                 @RequestBody Map<String, Object> updatedBookmark) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElse(null);

        if (bookmark == null) {
            return "Bookmark: " + bookmarkId + " does not exist";
        }

        bookmark.setCharityName((String) updatedBookmark.get("charityName"));
        bookmarkRepository.save(bookmark);

        return "Update successful";
    }

    @Transactional
    @DeleteMapping("/bookmark/{bookmarkId}")
    public String deleteBookmark(@PathVariable("bookmarkId") Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElse(null);

        if (bookmark == null) {
            return "Bookmark: " + bookmarkId + " does not exist";
        }

        bookmarkRepository.deleteById(bookmarkId);

        return "Deleted bookmark";
    }
}
