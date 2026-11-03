package fr.library;

import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		books.forEach(System.out::println);
	}

	public Book findBookByTitle(String title) {
		return books.stream()
            .filter(book -> book.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new BookNotFoundException("Livre introuvable : " + title));
	}

	public Book findByIsbn(String isbn) {
		if (isbn == null) {
			throw new UnsupportedOperationException("Isbn doit être fournit");
		}

		return books.stream()
				.filter(book -> book.getIsbn().equals(isbn))
				.findFirst()
				.orElseThrow(() -> new BookNotFoundException("Livre introuvable dans la base"));
	}

	public Set<Book> findByAuthor(String author) {
		return books.stream()
				.filter(book -> book.getAuthor().equals(author))
				.collect(Collectors.toSet());
	}

	public boolean containsIsbn(String isbn) {
		if (isbn == null || isbn.length() != 13) {
			return false;
		}
		return books.stream().anyMatch(book -> book.getIsbn().equals(isbn));
	}

	public List<Book> booksSortedByTitle() {
		return books.stream()
				.sorted(Comparator.comparing(Book::getTitle))
				.toList();
	}

	public List<Book> booksSortedByAuthor() {
		return books.stream()
				.sorted(Comparator.comparing(Book::getAuthor)
						.thenComparing(Book::getTitle))
				.toList();
	}

	public List<Book> booksSortedByIsbn() {
		return books.stream()
				.sorted(Comparator.comparing(Book::getIsbn))
				.toList();
	}

	public List<Book> search(SearchCriteria criteria) {
		return books.stream()
				.filter(book -> switch (criteria) {
					case ByTitle(String title) -> book.getTitle().equalsIgnoreCase(title);
					case ByAuthor(String author) -> book.getAuthor().equals(author);
					case ByYearRange(int from, int to) -> book.getYear() >= from && book.getYear() <= to;
				})
				.toList();
	}

	public int size() {
		return books.size();
	}
}
