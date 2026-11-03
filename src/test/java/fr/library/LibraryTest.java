package fr.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LibraryTest {

    private Library library;

	@BeforeEach
	void setUp() {
        library = new Library();
		library.addBook(new Book("9782326002272", "Coder proprement", "Robbert C.Martin", 2008));
	}

    @Test
	@DisplayName("Cherche un livre par rapport à son titre")
    void shouldFindBookByTitle() {

        Book book = library.findBookByTitle("Coder proprement");

        assertEquals("9782326002272", book.getIsbn());
    }

    @Test
    @DisplayName("signale un titre absent")
    void shouldThrowExceptionWhenTitleIsNotFound() {
        assertThrows(BookNotFoundException.class,
                () -> library.findBookByTitle("Titre inconnu"));
    }

    @Test
	@DisplayName("Un livre ne peut pas exister 2 fois dans un librairy")
    void shouldThrowExceptionWhenAddingBookWithDuplicateIsbn() {
		Book book = new Book("9782326002272", "Coder proprement", "Robbert C.Martin", 2008);

		assertThrows(DuplicateBookException.class,
				() -> library.addBook(book));
    }

    @Test
	@DisplayName("chercher un livre non présent")
    void shouldThrowExceptionWhenFindNothingBook() {
		assertThrows(BookNotFoundException.class,
				() -> library.findByIsbn("1671676271635"));
    }

    @Test
    @DisplayName("supprime un livre de la bibliothèque")
    void shouldRemoveBook() {
        library.removeBook("9782326002272");

        assertEquals(0, library.size());
    }

    @Test
    @DisplayName("signale la suppression d'un livre absent")
    void shouldThrowExceptionWhenRemovingNothingBook() {
        assertThrows(BookNotFoundException.class,
                () -> library.removeBook("1671676271635"));
    }

    @Test
    @DisplayName("cherche les livres d'un auteur")
    void shouldFindBooksByAuthor() {
        Book secondBook = new Book("9780132350884", "Clean Code", "Robbert C.Martin", 2008);
        library.addBook(secondBook);

        Set<Book> books = library.findByAuthor("Robbert C.Martin");

        assertEquals(Set.of(secondBook, library.findBookByTitle("Coder proprement")), books);
    }

    @Test
    @DisplayName("retourne un ensemble vide si l'auteur est absent")
    void shouldReturnEmptySetWhenAuthorIsNotFound() {
        assertTrue(library.findByAuthor("Auteur inconnu").isEmpty());
    }

    @Test
    @DisplayName("vérifie la présence d'un ISBN")
    void shouldCheckIfIsbnExists() {
        assertTrue(library.containsIsbn("9782326002272"));
        assertFalse(library.containsIsbn("9780132350884"));
        assertFalse(library.containsIsbn("123"));
        assertFalse(library.containsIsbn(null));
    }

    @Test
    @DisplayName("retourne le nombre de livres")
    void shouldReturnLibrarySize() {
        assertEquals(1, library.size());
    }

    @Test
    @DisplayName("affiche les livres")
    void shouldDisplayBooks() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output));
            library.displayBooks();
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Coder proprement"));
    }

    @Test
    @DisplayName("signale un ISBN nul lors d'une recherche")
    void shouldThrowExceptionWhenSearchingWithNullIsbn() {
        assertThrows(UnsupportedOperationException.class,
                () -> library.findByIsbn(null));
    }

    @Test
    @DisplayName("refuse l'ajout d'un livre nul")
    void shouldThrowExceptionWhenAddingNullBook() {
        assertThrows(NullPointerException.class, () -> library.addBook(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "123", "9782326002272x"})
    @DisplayName("refuse les ISBN invalides")
    void shouldRejectInvalidIsbn(String isbn) {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(isbn, "Livre", "Auteur", 2026));

    }
}
