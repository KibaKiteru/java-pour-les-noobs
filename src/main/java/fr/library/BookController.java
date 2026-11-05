package fr.library;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getBooks(@RequestParam(defaultValue = "title") String sort) {
		try {
			var sortField = SortField.valueOf(sort.toUpperCase());
			return ResponseEntity.ok(bookService.findAll(sortField));

		} catch (IllegalArgumentException e) {
			return new ResponseEntity<List<Book>>(HttpStatus.BAD_REQUEST);
		} catch (NullPointerException e) {
			return new ResponseEntity<List<Book>>(HttpStatus.BAD_REQUEST);
		}
    }

    @PostMapping
    public ResponseEntity<Void> addBook(@RequestBody Book book) {
        bookService.addBook(book);
        return ResponseEntity.created(URI.create("/books/" + book.getIsbn())).build();
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> removeBook(@PathVariable String isbn) {
        bookService.removeBook(isbn);
        return ResponseEntity.noContent().build();
    }
}
