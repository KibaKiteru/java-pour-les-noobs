package fr.library;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LibraryExceptionHandler {

    @ExceptionHandler(DuplicateBookException.class)
    public ProblemDetail handleDuplicateBook(DuplicateBookException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ProblemDetail handleBookNotFound(BookNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
