package fr.library;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    private final Book cleanCode = new Book(
            "9780132350884", "Clean Code", "Robert C. Martin", 2008);

    @Test
    void getBooksShouldPassTheRequestedSortToTheService() throws Exception {
        given(bookService.findAll(SortField.AUTHOR)).willReturn(List.of(cleanCode));

        mockMvc.perform(get("/books").param("sort", "author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value("9780132350884"))
                .andExpect(jsonPath("$[0].title").value("Clean Code"));

        verify(bookService).findAll(SortField.AUTHOR);
    }

    @Test
    void getBooksWithoutSortShouldUseTitle() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());

        verify(bookService).findAll(SortField.TITLE);
    }

    @Test
    void unknownSortShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/books").param("sort", "unknown"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createBookShouldReturnCreatedBook() throws Exception {
        given(bookService.add(any(Book.class))).willReturn(cleanCode);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isbn":"9780132350884","title":"Clean Code",
                                 "author":"Robert C. Martin","year":2008}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("9780132350884"))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void duplicateBookShouldReturnConflict() throws Exception {
        willThrow(new DuplicateBookException("Livre déjà présent : 9780132350884"))
                .given(bookService).add(any(Book.class));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"isbn":"9780132350884","title":"Clean Code",
                                 "author":"Robert C. Martin","year":2008}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail")
                        .value("Livre déjà présent : 9780132350884"));
    }

    @Test
    void bookWithoutIsbnShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","author":"Robert C. Martin","year":2008}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBookShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/books/9780132350884"))
                .andExpect(status().isNoContent());

        verify(bookService).delete("9780132350884");
    }

    @Test
    void deleteUnknownBookShouldReturnNotFound() throws Exception {
        willThrow(new BookNotFoundException("Livre introuvable : 9780000000000"))
                .given(bookService).delete("9780000000000");

        mockMvc.perform(delete("/books/9780000000000"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {"status":404,"detail":"Livre introuvable : 9780000000000"}
                        """));
    }
}
