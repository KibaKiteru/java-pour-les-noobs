package fr.library;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Teste uniquement BookController, sans démarrer toute l'application.
@WebMvcTest(BookController.class)
class BookControllerTest {

    // Spring fournit un outil pour envoyer des requêtes HTTP au contrôleur.
    @Autowired
    private MockMvc mockMvc;

    // Remplace le vrai service par un faux objet contrôlé par Mockito.
    @MockitoBean
    private BookService bookService;

	private List<Book> books;

	@BeforeEach
    void setUp() {
        // Cette méthode est exécutée avant chaque test.
        // On prépare toujours la même liste de livres pour repartir d'un état connu.
        books = List.of(
                new Book("9780000000002", "Spring Boot", "Bob Dupont", 2021),
                new Book("9780000000001", "Java débutant", "Alice Martin", 2020),
                new Book("9780000000003", "Clean Code", "Robert Martin", 2008),
                new Book("9780000000004", "Java moderne", "Claire Durand", 2022),
                new Book("9780000000005", "Tests Java", "Alice Martin", 2021),
                new Book("9780000000006", "SQL pratique", "David Bernard", 2019),
                new Book("9780000000007", "Algorithmique", "Emma Petit", 2018),
                new Book("9780000000008", "JPA avancé", "Bob Dupont", 2023),
                new Book("9780000000009", "Design Patterns", "Robert Martin", 1994)
        );
    }

    @Test
    void shouldGetBooksSortedByTitle() throws Exception {
        // PRÉPARER : quand le contrôleur appellera le service avec "title",
        // le faux service devra renvoyer la liste books.

        // EXÉCUTER : on simule GET /books?sort=title.
        // perform() envoie la requête au contrôleur.
        // param() ajoute un paramètre dans l'URL.
        mockMvc.perform(get("/books").param("sort", "title"))
            // VÉRIFIER : le contrôleur doit répondre HTTP 200 (OK).
			.andExpect(status().isOk());

		verify(bookService, times(1)).findAll(SortField.TITLE);
    }

	@Test
	void shouldGetBooksSortedByAuthor() throws Exception {
		// PRÉPARER : le faux service renvoie books pour un tri par auteur.

		// EXÉCUTER : simuler GET /books?sort=author.
		mockMvc.perform(get("/books").param("sort", "author"))
		// VÉRIFIER : réponse HTTP 200 et liste JSON attendue.
			.andExpect(status().isOk());


		verify(bookService, times(1)).findAll(SortField.AUTHOR);
	}

	@Test
	void shouldGetBooksSortedByIsbn() throws Exception {
		// PRÉPARER : le faux service renvoie books pour un tri par ISBN.

		// EXÉCUTER : simuler GET /books?sort=isbn.
		mockMvc.perform(get("/books").param("sort", "isbn"))
		// VÉRIFIER : réponse HTTP 200 et liste JSON attendue.
			.andExpect(status().isOk());


		verify(bookService, times(1)).findAll(SortField.ISBN);
	}

    @Test
    void shouldGetBadrequestBooksbyInconu() throws Exception {
        // PRÉPARER : quand le contrôleur appellera le service avec "title",
        // le faux service devra renvoyer la liste books.

        // EXÉCUTER : on simule GET /books?sort=title.
        // perform() envoie la requête au contrôleur.
        // param() ajoute un paramètre dans l'URL.
        mockMvc.perform(get("/books").param("sort", "inconu"))
            // VÉRIFIER : le contrôleur doit répondre HTTP 200 (OK).
			.andExpect(status().isBadRequest());

		verify(bookService, times(0)).findAll(SortField.TITLE);
    }

    @Test
    void shouldCreateBook() throws Exception {
        // EXÉCUTER : simuler POST /books avec un livre au format JSON.
        mockMvc.perform(post("/books")
			// Indique au contrôleur que le corps est du JSON.
			.contentType(MediaType.APPLICATION_JSON)
			// content() place le livre à envoyer dans le corps de la requête.
			.content("{\"isbn\":\"9782326002272\",\"title\":\"Coder proprement\",\"author\":\"Robbert C.Martin\",\"year\":2008}"))
			// VÉRIFIER : une création réussie doit répondre HTTP 201 (Created).
			.andExpect(status().isCreated());

        // VÉRIFIER : le contrôleur a bien appelé addBook.
        // any(Book.class) signifie : n'importe quel objet de type Book.
        verify(bookService, times(1)).addBook(any(Book.class));
    }

    @Test
    void shouldReturnConflictWhenBookAlreadyExists() throws Exception {
        // PRÉPARER une erreur : si addBook reçoit n'importe quel Book,
        // le faux service lance une DuplicateBookException.
        doThrow(new DuplicateBookException("Livre déjà présent : 9782326002272"))
			.when(bookService).addBook(any(Book.class));

        // EXÉCUTER : envoyer la même requête de création.
        mockMvc.perform(post("/books")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"isbn\":\"9782326002272\",\"title\":\"Coder proprement\",\"author\":\"Robbert C.Martin\",\"year\":2008}"))
			// VÉRIFIER : l'erreur de doublon devient HTTP 409 (Conflict).
			.andExpect(status().isConflict())
			// VÉRIFIER le corps JSON de l'erreur.
			.andExpect(content().json("{\"status\":409,\"detail\":\"Livre déjà présent : 9782326002272\"}"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
		// EXÉCUTER : simuler DELETE /books/{isbn}.
        mockMvc.perform(delete("/books/9782326002272"))
			// VÉRIFIER : une suppression réussie répond HTTP 204 (No Content).
			.andExpect(status().isNoContent());

		// VÉRIFIER : removeBook a été appelé avec cet ISBN précis.
        verify(bookService).removeBook("9782326002272");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownBook() throws Exception {
		// PRÉPARER une erreur : removeBook doit échouer pour cet ISBN précis.
		// eq() signifie : l'argument doit être exactement égal à cette valeur.
        doThrow(new BookNotFoundException("Livre introuvable dans la base"))
			.when(bookService).removeBook(eq("9782326002272"));

		// EXÉCUTER : essayer de supprimer le livre inconnu.
        mockMvc.perform(delete("/books/9782326002272"))
			// VÉRIFIER : l'erreur devient HTTP 404 (Not Found).
			.andExpect(status().isNotFound())
			// VÉRIFIER le message d'erreur renvoyé en JSON.
			.andExpect(content().json("{\"status\":404,\"detail\":\"Livre introuvable dans la base\"}"));
    }
}
