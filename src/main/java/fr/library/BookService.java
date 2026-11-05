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

    public Book add(Book book) {
        library.addBook(book);
        return book;
    }

    public void delete(String isbn) {
        library.removeBook(isbn);
    }
}
