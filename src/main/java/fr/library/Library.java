package fr.library;

import java.util.HashSet;
import java.util.Set;

public class Library {

	private final Set<Book> books = new HashSet<>();

	public void addBook(Book book) {
		if (book == null)
		{
			throw new NullPointerException("on ne peut pas ajouter un livre null");
		}
		if (containsIsbn(book.getIsbn())) {
			throw new DuplicateBookException("Il ne peut pas y avoir deux book avec le même isbn");

		}
		books.add(book);
	}

	public void removeBook(String isbn) {
		boolean removed = books.removeIf(book -> isbn != null && isbn.equals(book.getIsbn()));
		if (!removed) {
			throw new BookNotFoundException("Livre introuvable dans la base");
		}
	}

	public void displayBooks() {

		for (Book book: books) {
			System.out.println(book);
		}
	}

	public Book findBookByTitle(String title) {
		for (Book book: books) {
			if (book.getTitle().equals(title)) {
				return book;
			}
		}
		throw new BookNotFoundException("Livre introuvable dans la base");
	}

	public Book findByIsbn(String isbn) {
		if (isbn == null) {
			throw new UnsupportedOperationException("Isbn doit être fournit");
		}

		for (Book book: books) {
			if (book.getIsbn().equals(isbn)) {
				return book;
			}
		}
		throw new BookNotFoundException("Livre introuvable dans la base");
	}

	public Set<Book> findByAuthor(String author) {
		Set<Book> booksAuthor = new HashSet<>();
		for (Book book: books) {
			if (book.getAuthor().equals(author)) {
				booksAuthor.add(book);
			}
		}
		return booksAuthor;
	}

	public boolean containsIsbn(String isbn) {
		if (isbn == null || isbn.length() != 13) {
			return false;
		}
		for (Book book: books) {
			if (book.getIsbn().equals(isbn)) {
				return true;
			}
		}
		return false;
	}

	public int size() {
		return books.size();
	}
}
