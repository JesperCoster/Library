package view;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.*;

import java.time.LocalDate;
import java.util.List;

public class BooksPane extends VBox {
    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private ComboBox<SearchMode> searchModeBox;
    private ComboBox<Integer> searchByRatingBox;
    private ComboBox<Integer> setRatingBox;
    private TextField searchField;
    private Button searchButton;
    private Button searchByRatingButton;

    private MenuBar menuBar;
    private TextField firstName;
    private TextField lastName;
    private TextField genreName;
    private TextField bookTitle;
    private TextField isbn;
    final Controller controller;

    public BooksPane(Controller controller) {
        this.controller = controller;
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus();

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton, searchByRatingBox, searchByRatingButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false); // don't allow user updates (yet)
        booksTable.setPlaceholder(new Label("No rows to display"));

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        //TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        //TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        booksTable.getColumns().addAll(titleCol, isbnCol);
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));

        // define how to fill data for each cell,
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        //authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        //genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");


        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                onSearchSelected(searchFor, mode, 0);
            }
        });

        searchByRatingButton = new Button("Search by rating");
        searchByRatingBox = new ComboBox<Integer>();
        searchByRatingBox.setPromptText("Rating");
        searchByRatingBox.getItems().addAll(1,2,3,4,5);

        searchByRatingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                String searchFor = "rating";
                int ratingToSearch = searchByRatingBox.getValue();
                SearchMode mode = SearchMode.Rating;
                onSearchSelected(searchFor, mode, ratingToSearch);

            }
        });


    }

    private void initMenus() {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem connectItem = new MenuItem("Connect to Db");
        MenuItem disconnectItem = new MenuItem("Disconnect");
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        Menu searchMenu = new Menu("Search");
        MenuItem titleItem = new MenuItem("Title");
        MenuItem isbnItem = new MenuItem("ISBN");
        MenuItem authorItem = new MenuItem("Author");
        searchMenu.getItems().addAll(titleItem, isbnItem, authorItem);

        Menu manageMenu = new Menu("Manage");
        MenuItem addBook = new MenuItem("Add Book");
        MenuItem addAuthor = new MenuItem("Add Author");
        MenuItem addGenre = new MenuItem("Add Genre");
        MenuItem addRating = new MenuItem("Add Rating");
        MenuItem removeItem = new MenuItem("Remove");
        MenuItem updateItem = new MenuItem("Update");
        manageMenu.getItems().addAll(addBook, addAuthor, addGenre, addRating, removeItem, updateItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu);
        addBook.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dialog dialog = new Dialog();
                dialog.setTitle("add a book");
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                dialog.getDialogPane().setContent(createNewBookForm());


                dialog.show();

            }
        });
        addAuthor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dialog dialog = new Dialog();
                dialog.setTitle("add an author");
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                dialog.getDialogPane().setContent(createAuthorForm());

                dialog.show();

            }
        });
        addGenre.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dialog dialog = new Dialog();
                dialog.setTitle("add a new genre");
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                dialog.getDialogPane().setContent(createGenreForm());

                dialog.show();

            }
        });
        addRating.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Dialog dialog = new Dialog();
                dialog.setTitle("add a new rating");
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                dialog.getDialogPane().setContent(createRatingForm());

                dialog.show();

            }
        });



    }
    private Node createGenreForm()  {
        genreName = new TextField();

        GridPane gridPane = new GridPane();
        gridPane.add(new Label("Genre name:"), 0, 0);

        gridPane.add(genreName, 1, 0);

        Button addGenreButton = new Button("create");
        gridPane.add(addGenreButton, 1, 1);
        addGenreButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String genreNameToAdd = genreName.getText();
                /*Thread t = new Thread(() -> {
                    try {
                        controller.addGenre(genreNameToAdd);
                    } catch (Exception e) {
                        showAlertAndWait("Database error.",ERROR);
                    }
                    Platform.runLater(() -> {
                        genreName.clear();
                    });

                });
                t.start();*/
                onAddNewGenre(genreNameToAdd);


            }
        });

        return gridPane;

    }
    private Node createAuthorForm()  {
        firstName = new TextField();
        lastName = new TextField();
        GridPane gridPane = new GridPane();
        gridPane.add(new Label("First name:"), 0, 0);
        gridPane.add(new Label("Last name"), 0, 1);
        gridPane.add(new Label("Date of birth"), 0, 2);

        gridPane.add(firstName, 1, 0);
        gridPane.add(lastName, 1, 1);

        DatePicker authorDoBPicker = new DatePicker();
        authorDoBPicker.getEditor().setDisable(true);
        gridPane.add(authorDoBPicker, 1,2);
        Button addAuthorButton = new Button("add author");
        gridPane.add(addAuthorButton, 1, 4);
        addAuthorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fName = firstName.getText();
                String lName = lastName.getText();

                //java.util.Date date = java.util.Date.from(authorDoBPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                //Date doBAuthor = new java.sql.Date(date.getTime());
                LocalDate doBAuthor = authorDoBPicker.getValue();
                /*Thread t = new Thread(() -> {
                    try {
                        controller.addNewAuthor(fName, lName, doBAuthor);
                    } catch (Exception e) {
                        showAlertAndWait("Database error.",ERROR);
                    }
                    Platform.runLater(() -> {
                        firstName.clear();
                        lastName.clear();
                        authorDoBPicker.getEditor().clear();
                    });

                });
                t.start();*/
                onAddNewAuthor(fName, lName, doBAuthor, authorDoBPicker);


            }
        });

        return gridPane;

    }

    private Node createNewBookForm()
    {
        GridPane gridPane = new GridPane();
        bookTitle = new TextField();
        isbn = new TextField();
        Button addNewBookButton = new Button("Add Book");
        gridPane.add(new Label("Title:"), 0, 0);
        gridPane.add(new Label("ISBN"), 0, 1);
        gridPane.add(bookTitle, 1, 0);
        gridPane.add(isbn, 1, 1);
        gridPane.add(new Label("Author"), 0, 2);
        gridPane.add(new Label("Genre"), 0, 3);
        gridPane.add(addNewBookButton, 0, 5);

        ComboBox comboBoxAuthor = new ComboBox<>();
        ComboBox comboBoxGenre = new ComboBox<>();
        gridPane.add(comboBoxAuthor, 1,2);
        gridPane.add(comboBoxGenre, 1,3);
        //List<? extends AuthorDTO> authors = new ArrayList<>();
        //final List<? extends GenreDTO> genres = new ArrayList<>();

        /*Thread t = new Thread(() -> {
            List<? extends AuthorDTO> authors = null;
            List<? extends GenreDTO> genres = null;
            try {
                authors = controller.getAllAuthors();
                genres = controller.getAllGenres();

            }catch (Exception e){
                showAlertAndWait("Database error.",ERROR);
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
        t.start();*/
        onAddNewBookForm(comboBoxAuthor, comboBoxGenre);




        addNewBookButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String bookTitleToAdd = bookTitle.getText();
                String isbnToAdd = isbn.getText();


                //Integer authorIdToAdd = (Author) comboBoxAuthor.getSelectionModel().getSelectedItem();
                //Genre genreIdToAdd = (Genre) combo

                AuthorDTO author = (AuthorDTO) comboBoxAuthor.getSelectionModel().getSelectedItem();
                GenreDTO genre = (GenreDTO) comboBoxGenre.getSelectionModel().getSelectedItem();
                int[] authorIdToAdd = {author.getAuthorId()};
                int[] genreIdToAdd = {genre.getGenreId()};

                /*Thread t = new Thread(() -> {
                    try {
                        controller.addBook(isbnToAdd, bookTitleToAdd, authorIdToAdd, genreIdToAdd);
                    } catch (Exception e) {
                        showAlertAndWait("Database error.",ERROR);
                    }
                    javafx.application.Platform.runLater(() -> {
                        bookTitle.clear();
                        isbn.clear();
                        comboBoxGenre.setValue(null);
                        comboBoxAuthor.setValue(null);
                    });

                });
                t.start();*/
                onAddNewBook(isbnToAdd, bookTitleToAdd, authorIdToAdd,
                        genreIdToAdd, comboBoxGenre, comboBoxAuthor);

            }
        });
        return gridPane;

    }
    private Node createRatingForm()  {
        GridPane gridPane = new GridPane();
        ListView<BookDTO> booksList = new ListView<BookDTO>();

        setRatingBox = new ComboBox<Integer>();
        setRatingBox.setPromptText("Rating");
        setRatingBox.getItems().addAll(1,2,3,4,5);
        gridPane.add(setRatingBox,1,0);
        gridPane.add(booksList,0,0);
        Button addNewRatingButton = new Button("add new Rating");

        gridPane.add(addNewRatingButton, 0, 2);

        onAddNewRatingForm(booksList);
        /*Thread t = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<? extends BookDTO> books = null;
            try {
                books = controller.getAllBooks();
            }catch (Exception e){
                showAlertAndWait("Database error.",ERROR);

            }
            List<? extends BookDTO> finalBooks = books;
            javafx.application.Platform.runLater(() -> {
                for (int i = 0; i < finalBooks.size(); i++){
                    booksList.getItems().add(finalBooks.get(i));
                }
            });

        });
        t.start();*/



        addNewRatingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int ratingToAdd = setRatingBox.getValue();
                BookDTO selectedBook = booksList.getSelectionModel().getSelectedItem();
                /*Thread t = new Thread(() -> {
                    try {
                        controller.addNewRating(selectedBook.getIsbn(), ratingToAdd);
                    } catch (Exception e) {
                        showAlertAndWait("Database error.",ERROR);
                    }
                    Platform.runLater(() -> {
                        showAlertAndWait(
                                "rating added!", INFORMATION);
                    });

                });
                t.start();*/
                onAddNewRating(ratingToAdd, selectedBook);

            }
        });

        return gridPane;

    }



    protected void onSearchSelected(String searchFor, SearchMode mode, int searchRating) {
        ThreadController thread = new ThreadController();
        thread.SearchThread(searchFor, mode, searchRating,controller, this);
    }

    protected void onAddNewRatingForm(ListView<BookDTO> booksList)
    {
        ThreadController thread = new ThreadController();
        thread.GetBooksRatingForm(controller, this,booksList);

    }
    protected void onAddNewRating(int ratingToAdd, BookDTO selectedBook)
    {
        ThreadController thread = new ThreadController();
        thread.addNewrating(controller, this, selectedBook, ratingToAdd);
    }

    protected void onAddNewBookForm(ComboBox comboBoxAuthor, ComboBox comboBoxGenre)
    {
        ThreadController thread = new ThreadController();
        thread.addNewBookForm(controller,this, comboBoxAuthor, comboBoxGenre);
    }

    protected void onAddNewBook(String isbnToAdd, String bookTitleToAdd, int[] authorIdToAdd,
                                int[] genreIdToAdd, ComboBox comboBoxGenre, ComboBox comboBoxAuthor)
    {
        ThreadController thread = new ThreadController();
        thread.addNewBook(controller, this, isbnToAdd, bookTitleToAdd, authorIdToAdd,
        genreIdToAdd, comboBoxGenre, comboBoxAuthor, bookTitle, isbn);
    }

    protected void onAddNewAuthor(String fName, String lName, LocalDate doBAuthor, DatePicker authorDoBPicker)
    {
        ThreadController thread = new ThreadController();
        thread.addNewAuthor(controller, this, fName, lName, doBAuthor, firstName,
                lastName, authorDoBPicker);
    }

    protected void onAddNewGenre(String genreNameToAdd)
    {
        ThreadController thread = new ThreadController();
        thread.addNewGenre(controller, this, genreNameToAdd, genreName);
    }
}
