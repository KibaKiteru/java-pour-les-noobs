package fr.library;

public class Main {

    public static void main(String[] args) {

        Library library = new Library();

        library.addBook(new Book( "9780132350884",  "Clean Code",  "Robert Martin",  2008));
        library.addBook(new Book("9780134685991", "Effective Java", "Joshua Bloch", 2018));

        library.displayBooks();
    }
}
