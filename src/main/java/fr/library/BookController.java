package fr.library;

import java.util.List;

import java.net.URI;
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
        var sortField = SortField.valueOf(sort.toUpperCase());
        return ResponseEntity.ok(bookService.findAll(sortField));
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody BookRequest request) {
        Book book = bookService.add(request.toBook());
        return ResponseEntity.created(URI.create("/books/" + book.getIsbn())).body(book);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> removeBook(@PathVariable String isbn) {
        bookService.delete(isbn);
        return ResponseEntity.noContent().build();
    }
}
