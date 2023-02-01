package controller;

import integration.LibraryDAOImpl2;
import integration.LibraryDBException;
import model.Author;
import model.Book;
import model.Genre;

import java.time.LocalDate;
import java.util.List;

public class Controller {
    private final LibraryDAOImpl2 libraryDAO;

    public Controller() {
        this.libraryDAO = new LibraryDAOImpl2();
    }

    public List<Author> getAllAuthors() throws LibraryDBException {
        return libraryDAO.findAllAuthors();

    }
    public List<Book> getAllBooks() throws LibraryDBException{
        return libraryDAO.findAllBooks();
    }
    public List<Book> getAllBooksByIsbn(String isbn) throws LibraryDBException{
        return libraryDAO.searchBooksByIsbn(isbn);
    }
    public List<Book> getAllBooksByTitle(String title) throws LibraryDBException{
        return libraryDAO.searchBooksByTitle(title);
    }
    public List<Book> getAllBooksByAuthor(String author) throws LibraryDBException{
        return libraryDAO.searchBooksByAuthor(author);
    }
    public List<Book> getAllBooksByGenre(String genreName) throws LibraryDBException{
        return libraryDAO.searchBooksByGenre(genreName);
    }
    public List<Book> getAllBooksByRating(int rating) throws LibraryDBException{
        return libraryDAO.searchBooksByRating(rating);
    }
    public void addBook(String isbn, String title, int[] authorIds, int[] genreIds) throws LibraryDBException {
        libraryDAO.createNewBook(isbn, title,authorIds, genreIds);
    }
    public void addGenre(String genreName) throws LibraryDBException {
        libraryDAO.createNewGenre(genreName);
    }

    public void addNewAuthor(String firstName, String lastName, LocalDate dob) throws LibraryDBException {
        libraryDAO.createNewAuthor(firstName, lastName, dob);
    }
    public List<Genre> getAllGenres() throws LibraryDBException {
        return libraryDAO.findAllGenres();
    }
    public void addNewRating(String isbn, int bookRate) throws  LibraryDBException {
        libraryDAO.createNewRating(isbn, bookRate);
    }
    public void disconnect() throws LibraryDBException {
        libraryDAO.disconnect();
    }



}
