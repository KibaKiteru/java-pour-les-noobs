package fr.library;

public class Main {

	public static void main(String[] args) {

		Library library = new Library();
		Book book1 = new Book( "9780132350884",  "Clean Code",  "Robert Martin",  2008);
		Book book2 = new Book( "9780134685991", "Effective Java", "Joshua Bloch", 2018);
		Book book3 = new Book( "9780134685992", "test", "Au revoir", 2000);

		library.addBook(book1);
		library.addBook(book2);
		try {
			library.addBook(book2);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		library.addBook(book3);
		System.out.println("size : " + library.size());
		library.displayBooks();
		library.removeBook(book3.getIsbn());
		System.out.println("size : " + library.size());

		library.displayBooks();
	}
}
