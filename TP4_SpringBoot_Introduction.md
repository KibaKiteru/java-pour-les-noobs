# 🟡 TP4 — Introduction à Spring Boot

⏱️ Durée indicative : 4 h

Ce TP transforme votre bibliothèque en API REST. Votre classe `Library` et ses tests ne changent pas : Spring Boot vient **autour**.

## 1. Passer le projet sous Spring Boot

1. Sur [start.spring.io](https://start.spring.io), générez un projet : **Gradle - Groovy**, **Java**, la dernière version stable de Spring Boot proposée par défaut, Group `fr`, Artifact `library` (le package devient `fr.library`), **Java 25**, dépendance **Spring Web**. Revoir comment cela a été fait dans [automated-tests](https://github.com/corentinbeuchet/automated-tests).
2. Dans votre dépôt, remplacez `build.gradle` et `settings.gradle` par ceux du projet généré, et ajoutez `src/main/java/fr/library/LibraryApplication.java` et `src/main/resources/application.properties`.
3. Supprimez `Main.java`, qui n'est plus utile, et gardez vos classes et vos tests.
4. `./gradlew bootRun` doit démarrer l'application sur http://localhost:8080.
5. `./gradlew test` : les tests des TP 2 et 3 doivent toujours passer.

## 2. Code de départ

### 📄 SortField.java

Le critère de tri demandé par le client :

``` java
public enum SortField {
    TITLE, AUTHOR, ISBN
}
```

### 📄 BookRequest.java

Ce que le client envoie en JSON pour créer un livre. Un `record` se transforme directement en objet Java, sans constructeur vide ni setters :

``` java
public record BookRequest(String isbn, String title, String author, int year) {

    public Book toBook() {
        return new Book(isbn, title, author, year);
    }
}
```

> Si l'ISBN ou le titre est invalide, `toBook()` lève l'`IllegalArgumentException` de votre constructeur (TP 1).

### 📄 BookService.java

``` java
@Service
public class BookService {

    private final Library library = new Library();

    // TODO 7 : implémenter les trois méthodes ci-dessous en déléguant à Library
}
```

> Réutilisez votre classe `Library` (et ses tests) plutôt que de recréer une liste dans le service.

| Méthode | Rôle |
|---|---|
| `List<Book> findAll(SortField sort)` | la liste triée selon `sort` (un `switch` sur l'enum et vos méthodes `booksSortedBy...` du TP 3) |
| `Book add(Book book)` | ajoute le livre et le renvoie |
| `void delete(String isbn)` | supprime le livre |

### 📄 BookController.java

``` java
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // TODO 8 : créer les trois endpoints du tableau ci-dessous
}
```

> `@RequestParam(defaultValue = "title") String sort` lit le paramètre `?sort=...` ; `SortField.valueOf(sort.toUpperCase())` le transforme en enum (et lève une `IllegalArgumentException` si la valeur est inconnue).

## 3. Reste à faire

### Exigences fonctionnelles

| Requête | Réponse |
|---|---|
| `GET /books?sort=title` (ou `author`, `isbn`) | `200` + la liste triée |
| `GET /books` | `200` + la liste triée par titre |
| `GET /books?sort=inconnu` | `400` |
| `POST /books` (livre en JSON) | `201` + le livre créé |
| `POST /books` avec un ISBN déjà présent | `409` |
| `POST /books` sans ISBN ou sans titre | `400` |
| `DELETE /books/{isbn}` | `204` |
| `DELETE /books/{isbn}` avec un ISBN inconnu | `404` |

Testez-les à la main avant d'écrire les tests, avec le client HTTP d'IntelliJ (fichier `.http`) ou `curl` (sous Windows, depuis Git Bash : dans PowerShell, `curl` désigne une autre commande) :

```bash
curl -i -X POST http://localhost:8080/books -H "Content-Type: application/json" -d '{"isbn":"9780132350884","title":"Clean Code","author":"Robert C. Martin","year":2008}'
curl -i "http://localhost:8080/books?sort=author"
```

### Contraintes techniques

-   Transformer les exceptions en codes HTTP avec `@RestControllerAdvice` + `@ExceptionHandler` (une méthode par exception : `BookNotFoundException`, `DuplicateBookException`, `IllegalArgumentException`)
-   Renvoyer les erreurs au format `ProblemDetail` : `{ "status": 409, "detail": "Livre déjà présent : 978…" }` (le message de votre exception), avec `ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage())`. C'est le contrat qu'attend le front Angular ([angular-pour-les-noobs](https://github.com/corentinbeuchet/angular-pour-les-noobs), TP 4) : il affiche le champ `detail`.
-   La CI doit continuer à passer (`./gradlew test`)

## 4. Tester le controller

### Ce qu'on teste ici, et ce qu'on ne teste pas

Le tri, l'ajout et la suppression sont déjà testés par vos tests de `Library` (TP 2 et 3). Ce qu'il reste à tester, c'est la **couche HTTP** : le controller lit-il bien la requête, appelle-t-il la bonne méthode du service, renvoie-t-il le bon code et le bon JSON ?

Pour cela, `@WebMvcTest` démarre **uniquement** le controller (et votre `@RestControllerAdvice`), sans le service. Le service est remplacé par un **mock** : un faux objet, créé par [Mockito](https://site.mockito.org/), dont on décide nous-mêmes les réponses.

| Mockito | Signification |
|---|---|
| `given(bookService.findAll(SortField.AUTHOR)).willReturn(liste)` | « si on t'appelle avec `AUTHOR`, renvoie `liste` » |
| `willThrow(exception).given(bookService).delete("978…")` | « si on t'appelle avec cet ISBN, lève cette exception » |
| `verify(bookService).findAll(SortField.AUTHOR)` | vérifie que le controller a bien appelé le service avec `AUTHOR` |

> ⚠️ Un mock renvoie **exactement** ce qu'on lui a demandé de renvoyer. Un test du controller ne peut donc **pas** prouver que la liste est triée : il prouve que le controller demande le bon tri au service. Le vrai test de bout en bout (HTTP → service → base de données) arrive au TP 5.

Ajoutez la dépendance de test dans `build.gradle` :

``` groovy
testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
```

### Tests fournis

Créez `src/test/java/fr/library/BookControllerTest.java` :

``` java
package fr.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;          // simule des requêtes HTTP, sans vrai serveur

    @MockitoBean
    private BookService bookService;  // le faux service

    private final Book cleanCode = new Book("9780132350884", "Clean Code", "Robert C. Martin", 2008);

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
    void deleteUnknownBookShouldReturn404() throws Exception {
        willThrow(new BookNotFoundException("Livre introuvable : 9780000000000"))
                .given(bookService).delete("9780000000000");

        mockMvc.perform(delete("/books/9780000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Livre introuvable : 9780000000000"));
    }
}
```

> Le premier test vérifie que `?sort=author` arrive au service sous la forme `SortField.AUTHOR` et que le JSON est bien construit. Il ne vérifie pas le tri, et c'est normal (voir l'encadré ci-dessus).

### Tests à écrire

Une ligne du tableau des exigences = au moins un test. Il vous reste :

| Cas à tester | Indice |
|---|---|
| `GET /books` sans paramètre demande le tri par titre | `verify(...)` |
| `GET /books?sort=inconnu` → `400` | le service n'est même pas appelé |
| `POST /books` → `201` et le livre dans la réponse | `post("/books").contentType(MediaType.APPLICATION_JSON).content("""{...}""")` et `any(Book.class)` |
| `POST /books` avec un ISBN déjà présent → `409` | `given(...).willThrow(...)` |
| `POST /books` sans ISBN → `400` | aucun mock à préparer : c'est votre constructeur de `Book` qui refuse |
| `DELETE /books/{isbn}` → `204` | `verify(...)` |

## ✅ Terminé quand…

- [ ] `./gradlew bootRun` démarre et chaque ligne du tableau des exigences fonctionne avec `curl`
- [ ] Chaque ligne du tableau des exigences est couverte par un test de `BookControllerTest`
- [ ] Les tests des TP 2 et 3 passent toujours
