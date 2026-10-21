package fr.library;

import java.util.Objects;

public class Book {

	private final String isbn;
	private String title;
	private String author;
	private int year;

	// TODO 1 : initialiser les champs
	public Book(String isbn, String title, String author, int year) {
		if ( isbn == null || title == null
			|| isbn.isBlank()
			|| title.isBlank()
			|| !isbn.matches("\\d{13}")) {
			throw new IllegalArgumentException();
		}
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.year = year;
	}

	// TODO 2 : Créer les getters
	public String getIsbn() {
		return isbn;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public int getYear() {
		return year;
	}


	// TODO 3 : Redéfinir toString()
	@Override
	public String toString() {
		return "\nIsbn : " + this.isbn + "\nTitle : " + this.title + "\nAuthor : " + this.author + "\nYear : " + this.year;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return Objects.equals(isbn, book.isbn);
	}

	@Override
	public int hashCode() {
        return Objects.hash(isbn);
	}
}
