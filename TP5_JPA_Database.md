# 🔴 TP5 — Base de données avec JPA

⏱️ Durée indicative : 4 h

## 1. Dépendances

Complétez le bloc `dependencies` de `build.gradle` (JPA, Liquibase, PostgreSQL, Testcontainers) :

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-liquibase'
    runtimeOnly 'org.postgresql:postgresql'

    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-data-jpa-test'
    testImplementation 'org.springframework.boot:spring-boot-testcontainers'
    testImplementation 'org.testcontainers:testcontainers-junit-jupiter'
    testImplementation 'org.testcontainers:testcontainers-postgresql'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
```

> Spring Boot 4 : `spring-boot-starter-web` s'appelle désormais `spring-boot-starter-webmvc`, Liquibase a son propre starter et les starters de test sont découpés par technologie. Les versions sont gérées par Spring Boot : ne les écrivez pas.

## 2. Configuration et migrations

-   Copiez le contenu de [`ressources-tp5/src`](ressources-tp5/src) dans votre dossier `src` : configuration (`application.yaml`, `application-dev.yml`) et migrations Liquibase (création de la table `books` et dix livres d'exemple).
-   Supprimez `src/main/resources/application.properties` généré par start.spring.io : la configuration est désormais dans `application.yaml`.
-   Pour lancer l'application en local, démarrez une base PostgreSQL :

```bash
docker run -d --name library-db -e POSTGRES_DB=library_db -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:18
```


## 3. Code de départ

### 📄 Book.java

``` java
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String isbn;
    private String title;
    private String author;
    private int year;

    protected Book() {
        // requis par JPA
    }
}
```

> `@Table(name = "books")` est indispensable : la migration crée la table `books`, et `ddl-auto: validate` refuse de démarrer si l'entité pointe vers une table `book`.
>
> Gardez votre constructeur public (avec ses vérifications), vos getters, `equals`/`hashCode` sur l'ISBN et `toString()` : seuls les annotations, l'`id` et le constructeur vide s'ajoutent.
>
> Pourquoi les champs ne sont-ils plus `final` ? JPA crée l'objet avec le constructeur vide, **puis** remplit les champs : ils ne peuvent donc pas être `final`. L'ISBN reste immuable autrement : pas de setter, et `updatable = false` empêche JPA de le modifier en base.

### 📄 BookRepository.java

``` java
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    // TODO 9 : ajouter la méthode qui dit si un ISBN existe déjà (Spring Data la génère à partir de son nom)
}
```

### 📄 BookService.java

``` java
// TODO 10 : Utiliser BookRepository au lieu de la classe Library en mémoire.
// Les trois méthodes du TP 4 gardent la même signature : le controller ne change pas.
```

> Pour le tri, `bookRepository.findAll(Sort.by("title"))` demande à la base de trier.

## Reste à faire

### Exigences fonctionnelles

-   Le projet fonctionne avec une base de données PostgreSQL
-   Les endpoints du TP 4 se comportent exactement comme avant (mêmes codes HTTP)

### Contraintes techniques

-   Utiliser Spring Data JPA pour accéder aux données
-   Tests d'intégration sur une vraie base grâce à Testcontainers : Docker démarre un PostgreSQL 18 jetable pour les tests. Créez `src/test/java/fr/library/TestcontainersConfiguration.java` :

```java
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        // Version fixée (et non "latest") : les tests ne changent pas de comportement du jour au lendemain
        return new PostgreSQLContainer(DockerImageName.parse("postgres:18"));
    }
}
```

   `PostgreSQLContainer` est dans `org.testcontainers.postgresql` (Testcontainers 2).
-   Ne jamais committer de vrai mot de passe : le mot de passe de la base est lu dans la variable d'environnement `DB_PASSWORD`

## 4. Le test d'intégration

Au TP 4, `BookControllerTest` remplaçait le service par un mock : il testait le controller **seul**. Gardez-le, il est toujours valable.

On ajoute maintenant un test **de bout en bout** : une requête HTTP traverse le controller, le vrai service, JPA, jusqu'à une vraie base PostgreSQL. C'est ce test qui prouve enfin que `GET /books?sort=title` renvoie une liste triée.

| | `BookControllerTest` (TP 4) | `BookApiIT` (TP 5) |
|---|---|---|
| Annotation | `@WebMvcTest` | `@SpringBootTest` + `@AutoConfigureMockMvc` |
| Ce qui démarre | le controller seul | toute l'application + PostgreSQL (Testcontainers) |
| `BookService` | un mock | le vrai |
| Vitesse | rapide | plus lent (Docker) |

### Test fourni

Créez `src/test/java/fr/library/BookApiIT.java` :

``` java
package fr.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Test d'intégration de bout en bout : HTTP -> controller -> service -> JPA -> vraie base PostgreSQL
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional // chaque test est annulé à la fin : le suivant repart d'une base vide
class BookApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        // Volontairement dans le désordre : sinon le test passerait même sans tri
        bookRepository.save(new Book("9780134685991", "Effective Java", "Joshua Bloch", 2018));
        bookRepository.save(new Book("9780132350884", "Clean Code", "Robert C. Martin", 2008));
    }

    @Test
    void getBooksShouldReturnBooksSortedByTitle() throws Exception {
        mockMvc.perform(get("/books").param("sort", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[1].title").value("Effective Java"));
    }
}
```

> La base de test ne contient pas les dix livres d'exemple : leur migration est réservée au contexte `dev` (voir `002-insert-initial-books.yaml`). Seuls les livres du `setUp()` sont présents.
> Docker doit être lancé pour exécuter ce test.

### Tests à écrire

Cette fois, vérifiez aussi l'état de la base avec `bookRepository` :

| Cas à tester | Vérification |
|---|---|
| `POST /books` → `201` | le livre est bien en base |
| `POST /books` avec un ISBN déjà présent → `409` | la base contient toujours 2 livres |
| `DELETE /books/{isbn}` → `204` | le livre n'est plus en base |
| `DELETE /books/{isbn}` avec un ISBN inconnu → `404` | |

## ✅ Terminé quand…

- [ ] `./gradlew bootRun` démarre avec PostgreSQL et `GET /books` renvoie les dix livres d'exemple
- [ ] `BookApiIT` tourne sur PostgreSQL via Testcontainers, en local et dans la CI
- [ ] Les tests des TP 2 à 4 (dont `BookControllerTest`) passent toujours
