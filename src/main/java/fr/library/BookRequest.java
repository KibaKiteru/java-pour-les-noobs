package fr.library;

public record BookRequest(String isbn, String title, String author, int year) {

    public Book toBook() {
        return new Book(isbn, title, author, year);
    }
}
