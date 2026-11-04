# 🟢 TP3 — Java moderne

⏱️ Durée indicative : 2 h

Java a beaucoup évolué depuis Java 8. Ce TP vous fait utiliser ce que l'on trouve dans le code d'aujourd'hui : Streams, `Optional`, `record`, `sealed interface` et `switch` avec *pattern matching*.

## 1. Streams

Remplacez la boucle de recherche par un Stream :

``` java
public Book findBookByTitle(String title) {
    return books.stream()
            .filter(book -> book.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new BookNotFoundException("Livre introuvable : " + title));
}
```

> `books` représente ici votre collection de livres : si vous utilisez une `Map`, partez de `books.values().stream()`.
> `findFirst()` renvoie un `Optional<Book>` : `orElseThrow(...)` conserve l'exception du TP 2.

-   Retourner la liste des livres triée par titre : `List<Book> booksSortedByTitle()`
-   Retourner la liste des livres triée par auteur, puis par titre : `List<Book> booksSortedByAuthor()`
-   Retourner la liste des livres triée par ISBN : `List<Book> booksSortedByIsbn()` (elle servira au TP 4)
-   Utiliser `sorted(...)` avec un [`Comparator`](https://www.baeldung.com/java-comparator-comparable) : `Comparator.comparing(Book::getAuthor).thenComparing(Book::getTitle)`
-   Terminer vos Streams par `.toList()` (liste non modifiable) plutôt que `collect(Collectors.toList())`

## 2. `record`, `sealed interface` et `switch`

Au lieu d'écrire une méthode par type de recherche, on veut **une seule** méthode `search`. Créez les critères de recherche (**un fichier par type**) :

``` java
public sealed interface SearchCriteria permits ByTitle, ByAuthor, ByYearRange {
}

public record ByTitle(String title) implements SearchCriteria { }
public record ByAuthor(String author) implements SearchCriteria { }
public record ByYearRange(int from, int to) implements SearchCriteria { }
```

Puis implémentez dans `Library` :

``` java
public List<Book> search(SearchCriteria criteria) {
    return books.stream()
            .filter(book -> switch (criteria) {
                case ByTitle(String title) -> // TODO
                case ByAuthor(String author) -> // TODO
                case ByYearRange(int from, int to) -> // TODO
            })
            .toList();
}
```

> Le `switch` n'a pas besoin de `default` : l'interface est `sealed`, le compilateur sait que tous les cas sont traités. Ajoutez un quatrième record à la clause `permits` sans le traiter dans le `switch` : que se passe-t-il ?

## 3. Tester un tri

Créez `src/test/java/fr/library/LibrarySearchTest.java` avec ce premier test :

``` java
class LibrarySearchTest {

    private static final Book CLEAN_CODE = new Book("9780132350884", "Clean Code", "Robert C. Martin", 2008);
    private static final Book CLEAN_ARCHITECTURE = new Book("9780134494166", "Clean Architecture", "Robert C. Martin", 2017);
    private static final Book EFFECTIVE_JAVA = new Book("9780134685991", "Effective Java", "Joshua Bloch", 2018);
    private static final Book DESIGN_PATTERNS = new Book("9780201633610", "Design Patterns", "Erich Gamma", 1994);

    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
        // Volontairement dans le désordre : ni par titre, ni par auteur, ni par ISBN
        library.addBook(EFFECTIVE_JAVA);
        library.addBook(CLEAN_CODE);
        library.addBook(DESIGN_PATTERNS);
        library.addBook(CLEAN_ARCHITECTURE);
    }

    @Test
    void shouldSortByTitle() {
        assertEquals(List.of(CLEAN_ARCHITECTURE, CLEAN_CODE, DESIGN_PATTERNS, EFFECTIVE_JAVA),
                library.booksSortedByTitle());
    }
}
```

> ⚠️ **Un test de tri ne prouve quelque chose que si la liste de départ n'est pas déjà triée.** Avec un seul livre, ou des livres ajoutés dans le bon ordre, le test passe même si votre méthode ne trie rien.
> Vérifiez-le : retirez le `.sorted(...)` de `booksSortedByTitle()`, relancez les tests. Le test doit devenir **rouge**. Remettez le tri.

## Tests à écrire

| Cas à tester |
|---|
| tri par auteur puis par titre (les deux livres de Robert C. Martin doivent être départagés par le titre) |
| tri par ISBN |
| trier une bibliothèque vide renvoie une liste vide |
| deux livres de même titre (ISBN différents) sont tous les deux conservés par le tri |
| un test par critère de recherche : `ByTitle` (sans tenir compte de la casse), `ByAuthor`, `ByYearRange`, et un critère qui ne trouve rien (un `@ParameterizedTest` avec `@MethodSource` est bienvenu) |

Pour chaque test de tri, faites la même vérification : sans le tri, il doit échouer.

## Contraintes techniques

-   `search(new ByTitle(...))` ne tient pas compte de la casse (`equalsIgnoreCase`)

## Bonus

-   `Optional<Book> findOptionalByIsbn(String isbn)` : quand préférer un `Optional` à une exception ? Répondez dans la Pull Request.
-   Regrouper les livres par auteur : `Map<String, List<Book>>` avec `Collectors.groupingBy`
-   Un [Collector personnalisé](https://www.baeldung.com/java-collectors)

## ✅ Terminé quand…

- [ ] Plus aucune boucle `for` dans `Library`
- [ ] `search(...)` gère les trois critères avec un `switch` sans `default`
- [ ] Chaque test de tri échoue si l'on retire le tri correspondant
- [ ] Tous les tests passent dans la CI
