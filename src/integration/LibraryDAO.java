package integration;

import model.Author;
import model.Book;
import model.Genre;

import java.time.LocalDate;
import java.util.List;

public interface LibraryDAO {
    /**
     * Connects the user to the database using a predetermined string containing where the database is
     * the name of the database and the user and password
     * @throws LibraryDBException
     */
    void connect() throws LibraryDBException;
    /**
     * Closes the connection to the database
     */
    void disconnect() throws LibraryDBException;
    /**
     * Creates a new book from the in parameters, and puts it in the db
     * @param isbn Takes in the ISBN of the new book in a String form
     * @param title Takes in the title of the new book in a String form
     * @param authorIds Takes in all id of the authors of the book in form of a int array
     * @param genreIds Takes in all the genre Id:s for the genres of the book, in a int array format
     * @throws LibraryDBException
     */
    void createNewBook  (String isbn, String title, int[] authorIds, int[] genreIds) throws LibraryDBException;
    /**
     * Puts in a new author in  the database
     * It takes in all attributes for a author and then creates it from these attributes
     * @param firstName takes the first name of the new author
     * @param lastName takes in the last name of the new author
     * @param dob takes in the dob of the new author
     * @throws LibraryDBException
     */
    void createNewAuthor(String firstName, String lastName, LocalDate dob) throws LibraryDBException;
    /**
     * Creates a new genre that books could have
     * @param genreName takes in the name of the genre
     * @throws LibraryDBException
     */
    void createNewGenre(String genreName) throws LibraryDBException;
    /**
     * Creates a new rating for book
     * @param isbn takes in the ISBN that the new rating is for
     * @param bookRate takes in the new rating that is for the book
     * @throws LibraryDBException
     */
    void createNewRating(String isbn, int bookRate) throws LibraryDBException;
    /**
     * Searches and finds all the books with a certain title
     * @param title Takes in the title that is going to be searched for
     * @return returns a Arraylist with all the books with a title that matches the in parameter
     * @throws LibraryDBException
     */
    List<Book> searchBooksByTitle(String title) throws LibraryDBException;
    /**
     * Searches and finds all the books with a certain ISBN, could be also be a partial ISBN
     * @param isbn the ISBN that is going to be searched for
     * @return returns a Arraylist with all the books that have a ISBN that matches the in parameter
     * @throws LibraryDBException
     */
    List<Book> searchBooksByIsbn(String isbn) throws LibraryDBException;
    /**
     * Finds all the books that have been written by a specific author
     * @param name the name of the author, could be the first name, last name or both
     * @return returns a Arraylist of all books that are written by the author
     * @throws LibraryDBException
     */
    List<Book> searchBooksByAuthor(String name) throws LibraryDBException;
    /**
     * Finds and returns all the books that have a specific genre
     * @param genreName the name of the genre that is going to be searched for
     * @return returns a Arraylist with all the books that have the genre
     * @throws LibraryDBException
     */
    List<Book> searchBooksByGenre(String genreName) throws LibraryDBException;
    /**
     * Finds all the books in the db with a specific rating
     * @param rating takes the rating that its going to search for in a int form
     * @return returns a Arraylist of all books that have the desired rating
     * @throws LibraryDBException
     */
    List<Book> searchBooksByRating(int rating) throws LibraryDBException;
    /**
     * Finds all the authors in the db
     * @return returns a Arraylist with all authors in the db
     * @throws LibraryDBException
     */
    List<Author> findAllAuthors() throws LibraryDBException;
    /**
     * Finds all genres in the db
     * @return returns a Arraylist with all the genres in the db
     * @throws LibraryDBException
     */
    List<Genre> findAllGenres() throws LibraryDBException;
    /**
     * Finds all books that are in the db
     * @return a Arraylist of all books that are in the db
     * @throws LibraryDBException
     */
    List<Book> findAllBooks() throws LibraryDBException;

}
