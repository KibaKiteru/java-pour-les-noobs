package fr.library;

import java.util.ArrayList;
import java.util.List;

public class Library {

    private final List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        // TODO 4 : Ajouter un livre
        if (containsIsbn(book.getIsbn())) {
            throw new UnsupportedOperationException("Il ne peut pas y avoir deux book avec le même isbn");
        }

        books.add(book);
    }

    public void removeBook(String isbn) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equals(isbn)){
                books.remove(i);
            }
        }
    }

    public void displayBooks() {
        // TODO 5 : Afficher tous les livres

        for (Book book: books) {
          System.out.println(book);
        }
        //throw new UnsupportedOperationException("TODO 5 : displayBooks");
    }

    public Book findBookByTitle(String title) {
        // TODO 6 : Rechercher un livre par titre
        for (Book book: books) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
        //throw new UnsupportedOperationException("TODO 6 : findBookByTitle");
    }

    public Book findByIsbn(String isbn) {
        for (Book book: books) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null;
    }

    public Book findByAuthor(String author) {
        List<Book> booksAuthor = new ArrayList<>();
        for (Book book: books) {
            if (book.getAuthor().equals(author)) {
                booksAuthor.add(book);
            }
        }
        return null;
    }
    public boolean containsIsbn(String isbn) {
        for (Book book: books) {
            if (book.getIsbn().equals(isbn)){
                return true;
            }
        }
        return false;
    }

    public int size() {
        return books.size();
    }
}
