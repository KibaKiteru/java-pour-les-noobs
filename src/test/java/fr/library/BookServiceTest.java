package fr.library;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BookServiceTest {

	private BookService bookService;

	@BeforeEach
	void setUp() {
		bookService = new BookService();
		bookService.add(new Book("9782326002272", "Coder proprement", "Robbert C.Martin", 2008));
		bookService.add(new Book("9780000000002", "Spring Boot", "Bob Dupont", 2021));
		bookService.add(new Book("9780000000001", "Java débutant", "Alice Martin", 2020));
		bookService.add(new Book("9780000000003", "Clean Code", "Robert Martin", 2008));
	}


    @Test
	@DisplayName("Find all book")
    void shouldFindAllSortByTitle() {

		List<Book> res = bookService.findAll(SortField.TITLE);

		assertEquals(res, List.of(
			new Book("9780000000003", "Clean Code", "Robert Martin", 2008),
			new Book("9782326002272", "Coder proprement", "Robbert C.Martin", 2008),
			new Book("9780000000001", "Java débutant", "Alice Martin", 2020),
			new Book("9780000000002", "Spring Boot", "Bob Dupont", 2021)
		));
    }

	@Test
	@DisplayName("Find all books sorted by author")
	void shouldFindAllSortByAuthor() {
		List<Book> res = bookService.findAll(SortField.AUTHOR);

		assertEquals(List.of(
			new Book("9780000000001", "Java débutant", "Alice Martin", 2020),
			new Book("9780000000002", "Spring Boot", "Bob Dupont", 2021),
			new Book("9782326002272", "Coder proprement", "Robbert C.Martin", 2008),
			new Book("9780000000003", "Clean Code", "Robert Martin", 2008)
		), res);
	}

	@Test
	@DisplayName("Find all books sorted by ISBN")
	void shouldFindAllSortByIsbn() {
		List<Book> res = bookService.findAll(SortField.ISBN);

		assertEquals(List.of(
			new Book("9780000000001", "Java débutant", "Alice Martin", 2020),
			new Book("9780000000002", "Spring Boot", "Bob Dupont", 2021),
			new Book("9780000000003", "Clean Code", "Robert Martin", 2008),
			new Book("9782326002272", "Coder proprement", "Robbert C.Martin", 2008)
		), res);
	}

	@Test
	@DisplayName("Find no book when the library is empty")
	void shouldFindNoBookWhenLibraryIsEmpty() {
		BookService emptyBookService = new BookService();

		for (SortField sortField : SortField.values()) {
			assertTrue(emptyBookService.findAll(sortField).isEmpty());
		}
	}


}
