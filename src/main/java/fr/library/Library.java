package fr.library;

import java.util.ArrayList;
import java.util.List;

public class Library {

    private final List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        // TODO 4 : Ajouter un livre
        books.add(book);
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
}
