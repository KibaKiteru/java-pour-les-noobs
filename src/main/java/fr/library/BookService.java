package fr.library;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final Library library = new Library();

    public List<Book> getBooks(String sort) {
        return switch (sort == null || sort.isBlank() ? "title" : sort) {
            case "title" -> library.booksSortedByTitle();
            case "author" -> library.booksSortedByAuthor();
            case "isbn" -> library.booksSortedByIsbn();
            default -> throw new IllegalArgumentException("Tri inconnu : " + sort);
        };
    }

    public void addBook(Book book) {
        library.addBook(book);
    }

    public void removeBook(String isbn) {
        library.removeBook(isbn);
    }
}
