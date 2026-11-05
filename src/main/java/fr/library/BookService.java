package fr.library;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final Library library = new Library();

    public List<Book> findAll(SortField sort) {
        return switch (sort) {
            case TITLE -> library.booksSortedByTitle();
            case AUTHOR -> library.booksSortedByAuthor();
            case ISBN -> library.booksSortedByIsbn();
        };
    }

    public void addBook(Book book) {
        library.addBook(book);
    }

    public void removeBook(String isbn) {
        library.removeBook(isbn);
    }
}
