package fr.library;

public class Book {

	private final String isbn;
    private String title;
    private String author;
    private int year;

	// TODO 1 : initialiser les champs
    public Book(String isbn, String title, String author, int year) {
		if (isbn == null || title == null || isbn.isBlank() || title.isBlank())
		{
			throw new IllegalArgumentException();
		}
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.year = year;
    }

    // TODO 2 : Créer les getters
	public String getIsbn() {
		return isbn;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public int getYear() {
		return year;
	}


    // TODO 3 : Redéfinir toString()
	@Override
	public String toString() {
		return "\n" + "Isbn : " + this.isbn + "\n" + "Title : " + this.title + "\n" + "Author : " + this.author + "\n" + "Year : " + this.year;
	}
}
