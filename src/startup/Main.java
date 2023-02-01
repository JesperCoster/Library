package startup;

import controller.Controller;
import integration.LibraryDAOImpl;
import integration.LibraryDBException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.BooksPane;

import java.sql.Date;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Controller controller = new Controller();
        BooksPane root = new BooksPane(controller);


        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Books Database Client");
        // add an exit handler to the stage (X) ?
        primaryStage.setOnCloseRequest(event -> {
            try {
                controller.disconnect();
            } catch (Exception e) {}
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) throws LibraryDBException {
        //BooksPane bookView = new BooksPane(new Controller());
        launch(args);
        /*
        old code to test functions
        System.out.println("get authors: " + cont.getAllAuthors());
        System.out.println("get all books by title arry " + cont.getAllBooksByTitle("arry"));
        System.out.println("get all books by author owling: " + cont.getAllBooksByAuthor("owling"));
        System.out.println(" get all books by genre tasy: " + cont.getAllBooksByGenre("tasy"));

        System.out.println("creating book DIH");
        cont.addBook("5555", "Database illuminates Harry s2", new int[]{1,2}, new int[]{2,3});
        System.out.println("get all books by title s2" + cont.getAllBooksByTitle("s2") );

        //cont.addGenre("Drama");
        System.out.println(cont.getAllGenres());
        //cont.addNewAuthor("James", "Bond", Date.valueOf("1903-06-25"));
        //System.out.println("get authors: " + cont.getAllAuthors());
        cont.addNewRating("12345", 3);
        //cont.addNewRating("123456", 5);

         */





    }

}