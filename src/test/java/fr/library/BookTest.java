package fr.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BookTest {

	Book book;

	@BeforeEach
	void setUp() {
		book = new Book("9782326002272", "Coder proprement", "Robbert C.Martin", 2008);
	}

	@Test
	@DisplayName("Book have no Isbn")
	void shoulHasNoIsbn() {
		assertThrows(IllegalArgumentException.class,
				() -> new Book(null, "Le livre de la jungle", "Rudyard Kipling", 2018));
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@DisplayName("Book refuse un ISBN vide")
	void shouldRejectEmptyIsbn(String isbn) {
		assertThrows(IllegalArgumentException.class,
				() -> new Book(isbn, "Le livre de la jungle", "Rudyard Kipling", 2018));
	}

	@Test
	@DisplayName("Book expose ses informations")
	void shouldExposeBookInformation() {
		assertEquals("9782326002272", book.getIsbn());
		assertEquals("Coder proprement", book.getTitle());
		assertEquals("Robbert C.Martin", book.getAuthor());
		assertEquals(2008, book.getYear());
	}

	@Test
	@DisplayName("Book refuse un titre vide")
	void shouldRejectEmptyTitle() {
		assertThrows(IllegalArgumentException.class,
				() -> new Book("9782326002272", " ", "Auteur", 2026));
	}

	@Test
	@DisplayName("Book refuse un titre nul")
	void shouldRejectNullTitle() {
		assertThrows(IllegalArgumentException.class,
				() -> new Book("9782326002272", null, "Auteur", 2026));
	}
}
