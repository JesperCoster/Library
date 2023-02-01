package view;

import controller.Controller;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.*;


public class ThreadController {
    ThreadController()
    {}

    public void SearchThread(String searchFor, SearchMode mode, int searchRating, Controller controller, BooksPane booksPane)
    {

        Thread t = new Thread(() -> {
            List<? extends BookDTO> result = null;
            try {
                if (searchFor != null && searchFor.length() > 1) {

                    switch (mode) {
                        case Title:
                            result = controller.getAllBooksByTitle(searchFor);
                            break;
                        case ISBN:
                            result = controller.getAllBooksByIsbn(searchFor);
                            break;
                        case Author:
                            result = controller.getAllBooksByAuthor(searchFor);
                            break;
                        case Genre:
                            result = controller.getAllBooksByGenre(searchFor);
                            break;
                        case Rating:
                            result = controller.getAllBooksByRating(searchRating);
                            break;
                        default:
                            result= new ArrayList<>();
                    }

                }
            } catch (Exception e) {
                System.out.println("Database error.");
            }
            List<? extends BookDTO> finalResult = result;
            Platform.runLater(() -> {
                        if(searchFor == null || searchFor.length() < 1)
                        {
                            booksPane.showAlertAndWait(
                                    "Enter a search !", WARNING);
                        }
                        else {
                        if (finalResult == null || finalResult.isEmpty()) {
                            booksPane.showAlertAndWait(
                                    "No results found.", INFORMATION);
                        } else {
                            booksPane.displayBooks((List<Book>) finalResult);
                        }
                    }}
            );

        });
        t.start();
    }

    public void GetBooksRatingForm(Controller controller, BooksPane booksPane, ListView<BookDTO> booksList)
    {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<? extends BookDTO> books = null;
            try {
                books = controller.getAllBooks();
            }catch (Exception e){
                System.out.println("Database error.");
            }
            List<? extends BookDTO> finalBooks = books;
            javafx.application.Platform.runLater(() -> {
                for (int i = 0; i < finalBooks.size(); i++){
                    booksList.getItems().add(finalBooks.get(i));
                }
            });

        });
        t.start();
    }
    public void addNewrating(Controller controller, BooksPane booksPane, BookDTO selectedBook,  int ratingToAdd)
    {
        Thread t = new Thread(() -> {
            try {
                controller.addNewRating(selectedBook.getIsbn(), ratingToAdd);
            } catch (Exception e) {
                System.out.println("Database error.");
            }
            Platform.runLater(() -> {
                booksPane.showAlertAndWait(
                        "rating added!", INFORMATION);
            });

        });
        t.start();
    }

    public void addNewBookForm(Controller controller, BooksPane booksPane, ComboBox comboBoxAuthor, ComboBox comboBoxGenre)
    {
        Thread t = new Thread(() -> {
            List<? extends AuthorDTO> authors = null;
            List<? extends GenreDTO> genres = null;
            try {
                authors = controller.getAllAuthors();
                genres = controller.getAllGenres();

            }catch (Exception e){
                System.out.println("Database error.");
            }
            List<? extends AuthorDTO> finalAuthors = authors;
            List<? extends GenreDTO> finalGenres = genres;
            javafx.application.Platform.runLater(() -> {
                for(int i = 0; i < finalAuthors.size(); i++){
                    comboBoxAuthor.getItems().add(finalAuthors.get(i));
                }
                for (int i = 0; i < finalGenres.size(); i++){
                    comboBoxGenre.getItems().add(finalGenres.get(i));
                }
            });

        });
        t.start();

    }

    public void addNewBook(Controller controller, BooksPane booksPane, String isbnToAdd, String bookTitleToAdd, int[] authorIdToAdd,
                           int[] genreIdToAdd, ComboBox comboBoxGenre, ComboBox comboBoxAuthor, TextField bookTitle, TextField isbn)
    {
        Thread t = new Thread(() -> {
            try {
                controller.addBook(isbnToAdd, bookTitleToAdd, authorIdToAdd, genreIdToAdd);
            } catch (Exception e) {
                System.out.println("Database error.");
            }
            javafx.application.Platform.runLater(() -> {
                bookTitle.clear();
                isbn.clear();
                comboBoxGenre.setValue(null);
                comboBoxAuthor.setValue(null);
            });

        });
        t.start();
    }

    public void addNewAuthor(Controller controller, BooksPane booksPane, String fName, String lName, LocalDate doBAuthor, TextField firstName,
                             TextField lastName, DatePicker authorDoBPicker)
    {
        Thread t = new Thread(() -> {
            try {
                controller.addNewAuthor(fName, lName, doBAuthor);
            } catch (Exception e) {
                System.out.println("Database error.");
            }
            Platform.runLater(() -> {
                firstName.clear();
                lastName.clear();
                authorDoBPicker.getEditor().clear();
            });

        });
        t.start();
    }

    public void addNewGenre(Controller controller, BooksPane booksPane, String genreNameToAdd, TextField genreName)
    {
        Thread t = new Thread(() -> {
            try {
                controller.addGenre(genreNameToAdd);
            } catch (Exception e) {
                System.out.println("Database error.");
            }
            Platform.runLater(() -> {
                genreName.clear();
            });

        });
        t.start();
    }
}
