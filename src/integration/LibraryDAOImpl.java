package integration;

import model.Author;
import model.Book;
import model.Genre;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryDAOImpl implements LibraryDAO{
    private Connection con;
    private static final String TABLE_BOOK = "book";
    private static final String TABLE_AUTHOR = "author";
    private static final String TABLE_BOOK_AUTHOR = "book_author";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_ISBN = "isbn";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_BOOK_ID = "book_id";
    private static final String COLUMN_AUTHOR_ID = "author_id";
    private static final String  TABLE_GENRE = "genre";
    private static final String COLUMN_GENRE_ID = "genre_id";
    private static final String COLUMN_GENRE_TYPE = "genre_type";
    private static final String TABLE_RATING = "rating";
    private static final String COLUMN_RATING_SCORE = "rating_score";
    private static final String  TABLE_BOOK_GENRE= "book_genre";
    private PreparedStatement findAllAuthorsStmt;
    private PreparedStatement findBooksByTitleStmt;
    private PreparedStatement findBooksByRatingStmt;
    private PreparedStatement findBooksByGenreStmt;
    private PreparedStatement findBooksByISBNStmt;
    private PreparedStatement createNewBookStmt;
    private PreparedStatement insertAuthorToBookStmt;
    private PreparedStatement insertGenreToBookStmt;
    private PreparedStatement createNewGenreStmt;
    private PreparedStatement findAllGenresStmt;
    private PreparedStatement createNewAuthorStmt;
    private PreparedStatement createNewRatingToBookStmt;
    private PreparedStatement findBooksByAuthorStmt;
    private PreparedStatement checkIfRatingExistsStmt;
    private PreparedStatement updateRatingScoreStmt;
    private PreparedStatement findAllBooksStmt;

    public LibraryDAOImpl(){
        try {
            connect();
            prepareStatements();
        }
        catch (SQLException | LibraryDBException e){

        }
    }
    @Override
    public void connect() throws LibraryDBException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/lab1_librarydb", "testUser", "123");
            con.setAutoCommit(false);
        }
        catch(SQLException | ClassNotFoundException e){
            throw new LibraryDBException("could not connect to database");

        }


    }

    @Override
    public void disconnect() throws LibraryDBException {
        if(con != null){
            try{
                con.close();
                } catch (SQLException e){
                    throw new LibraryDBException("could not disconnect", e);
            }
        }
    }

    @Override
    public void createNewBook(String isbn, String title, int[] authorIds, int[] genreIds) throws LibraryDBException {
        try{
            createNewBookStmt.setString(1,isbn);
            createNewBookStmt.setString(2,title);
        }catch (SQLException e){
            handleException("Could not insert isbn, title in statement", e);
        }
        try{
            int updatedRows = createNewBookStmt.executeUpdate();
            if(updatedRows != 1) {
                handleException("could not update rows", null);
            }

            insertAuthorToBookStmt.setString(1, isbn);
            insertGenreToBookStmt.setString(1,isbn);

            for(int i = 0; i < authorIds.length; i++){
                insertAuthorToBookStmt.setInt(2, authorIds[i]);
                insertAuthorToBookStmt.executeUpdate();
            }
            for(int i = 0; i < genreIds.length; i++){
                insertGenreToBookStmt.setInt(2,genreIds[i]);
                insertGenreToBookStmt.executeUpdate();
            }

            con.commit();

        }catch (SQLException e){
            handleException("could not insert book into database ", e);
        }

    }

    @Override
    public void createNewAuthor(String firstName, String lastName, LocalDate dob) throws LibraryDBException {
        try{
            createNewAuthorStmt.setString(1,firstName);
            createNewAuthorStmt.setString(2,lastName);
            createNewAuthorStmt.setDate(3, Date.valueOf(dob));

            int n = createNewAuthorStmt.executeUpdate();
            if(n != 1){
                handleException("could not insert author", null);
            }
            con.commit();
        } catch (SQLException e){
            handleException("could not create author", e);
        }


    }

    @Override
    public void createNewGenre(String genreName) throws LibraryDBException {
        try{
            createNewGenreStmt.setString(1, genreName);
            int n = createNewGenreStmt.executeUpdate();
            if(n != 1){
                handleException("could not create", null);
             }

            con.commit();

        } catch (SQLException e){
            handleException("could not create new genre ", e);
        }

    }

    @Override
    public void createNewRating(String isbn, int bookRate) throws LibraryDBException {
        int n;
        ResultSet result = null;

        try {
            checkIfRatingExistsStmt.setString(1,isbn);
            result = checkIfRatingExistsStmt.executeQuery();
            result.next();

            if(result.getBoolean(1)){
                updateRatingScoreStmt.setInt(1, bookRate);
                updateRatingScoreStmt.setString(2, isbn);
                n = updateRatingScoreStmt.executeUpdate();
                if(n != 1){
                    handleException("could not update rating", null);
                }
            }else{
                createNewRatingToBookStmt.setString(1, isbn);
                createNewRatingToBookStmt.setInt(2, bookRate);
                n = createNewRatingToBookStmt.executeUpdate();
                if(n != 1){
                    handleException("could not create a new rating ", null);
                }
            }
            con.commit();
        } catch(SQLException e) {
            handleException("could not create new rating ", e);
        }
        finally {
            closeResultSet(result);
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String title) throws LibraryDBException {
        List<Book> books = new ArrayList<>();
        try{
            findBooksByTitleStmt.setString(1,title);
        }catch (SQLException e){
            handleException("Could not set title", e);
        }
        try(ResultSet result = findBooksByTitleStmt.executeQuery()){
            while(result.next()){
                books.add(new Book( result.getInt(COLUMN_BOOK_ID),
                        result.getString(COLUMN_ISBN),
                        result.getString(COLUMN_TITLE)));
            }
            con.commit();
        } catch (SQLException e){
            handleException("could not retrieve books by title ", e);
        }

        return books;
    }

    @Override
    public List<Book> searchBooksByIsbn(String isbn) throws LibraryDBException {
        List<Book> books = new ArrayList<>();
        try{
            findBooksByISBNStmt.setString(1,isbn);
        }catch (SQLException e){
            handleException("Could not set title", e);
        }
        try(ResultSet result = findBooksByISBNStmt.executeQuery()){
            while(result.next()) {
                books.add(new Book( result.getInt(COLUMN_BOOK_ID),
                        result.getString(COLUMN_ISBN),
                        result.getString(COLUMN_TITLE)));
            }
            con.commit();
        } catch (SQLException e){
            handleException("could not retrieve books by title ", e);
        }

        return books;
    }

    @Override
    public List<Book> searchBooksByAuthor(String name) throws LibraryDBException {
        List<Book> books = new ArrayList<>();
        try{ findBooksByAuthorStmt.setString(1,name);
            findBooksByAuthorStmt.setString(2,name);
        }catch(SQLException e){
            handleException("could not set author names", e);
        }
        try(ResultSet result = findBooksByAuthorStmt.executeQuery()) {
            while(result.next()){
                books.add(new Book( result.getInt(COLUMN_BOOK_ID),
                        result.getString(COLUMN_ISBN),
                        result.getString(COLUMN_TITLE)));
            }
            con.commit();
        }catch (SQLException e){
             String s ="SELECT " + COLUMN_ISBN + ", " + COLUMN_TITLE + " FROM " + TABLE_BOOK + " WHERE " + COLUMN_BOOK_ID + " IN " + "(SELECT " + COLUMN_BOOK_ID + " FROM " + TABLE_BOOK_AUTHOR + " WHERE " + COLUMN_AUTHOR_ID + " IN " + "(SELECT " + COLUMN_AUTHOR_ID + " FROM " + TABLE_AUTHOR + " WHERE " + COLUMN_FIRST_NAME + " RLIKE " + "?" + " OR " + COLUMN_LAST_NAME + " RLIKE " + "?" + "))";
            System.out.println(s);
             handleException("could not retrieve books by authors ", e);
        }
        return books;

    }

    @Override
    public List<Book> searchBooksByGenre(String genreName) throws LibraryDBException {
        List<Book> books = new ArrayList<>();
        try{
            findBooksByGenreStmt.setString(1,genreName);
        }catch (SQLException e){
            handleException("Could not set genreName", e);
        }
        try(ResultSet result = findBooksByGenreStmt.executeQuery()){
            while(result.next()) {
                books.add(new Book( result.getInt(COLUMN_BOOK_ID),
                        result.getString(COLUMN_ISBN),
                        result.getString(COLUMN_TITLE)));
            }
            con.commit();
        }catch (SQLException e){
            handleException("could not retrieve books by genre " , e);
        }
        return books;
    }

    @Override
    public List<Book> searchBooksByRating(int rating) throws LibraryDBException {
        List<Book> books = new ArrayList<>();
        try{
            findBooksByRatingStmt.setInt(1,rating);
        }catch (SQLException e){
            handleException("could not set rating in statement", e);
        }
        try(ResultSet result = findBooksByRatingStmt.executeQuery()){
            while(result.next()) {
                books.add(new Book( result.getInt(COLUMN_BOOK_ID),
                        result.getString(COLUMN_ISBN),
                        result.getString(COLUMN_TITLE)));
            }
            con.commit();
        }catch (SQLException e){
            handleException("could not retrieve books by genre ", e);
        }
        return books;
    }

    @Override
    public List<Author> findAllAuthors() throws LibraryDBException {
        List<Author> authors = new ArrayList<>();

        try(ResultSet result = findAllAuthorsStmt.executeQuery()) {
            while(result.next()) {
                authors.add(new Author(result.getInt(COLUMN_AUTHOR_ID),
                        result.getString(COLUMN_FIRST_NAME),
                        result.getString(COLUMN_LAST_NAME),
                        result.getDate(COLUMN_DOB)));
            }
            con.commit();
        } catch (SQLException e) {
            handleException("Could not retrieve Authors", e);
        }
        return authors;
    }

    @Override
    public List<Genre> findAllGenres() throws LibraryDBException {
        List<Genre> genres = new ArrayList<>();
        try(ResultSet result = findAllGenresStmt.executeQuery()){
            while(result.next()){
                genres.add( new Genre(result.getInt(COLUMN_GENRE_ID),
                        result.getString(COLUMN_GENRE_TYPE))
                );
            }
        } catch (SQLException e) {
            handleException("could not retrieve all genres", e);

        }

        return genres;
    }
    @Override
    public List<Book> findAllBooks() throws  LibraryDBException{
        List<Book> books = new ArrayList<>();

        try(ResultSet result = findAllBooksStmt.executeQuery()){
            while(result.next()){
                books.add(new Book(result.getInt(COLUMN_BOOK_ID),
                        result.getString(COLUMN_ISBN),
                        result.getString(COLUMN_TITLE)));
            }
            con.commit();
        }catch (SQLException e){
            handleException("could not retrieve books", e);
        }
        return books;

    }

    void prepareStatements() throws SQLException {
        findAllAuthorsStmt = con.prepareStatement("SELECT " + COLUMN_AUTHOR_ID + ", " + COLUMN_FIRST_NAME + ", " + COLUMN_LAST_NAME + ", " + COLUMN_DOB + " FROM " + TABLE_AUTHOR + ";");
        findBooksByTitleStmt = con.prepareStatement("SELECT " + COLUMN_BOOK_ID + ", " + COLUMN_ISBN + ", " + COLUMN_TITLE + " FROM " + TABLE_BOOK + " WHERE " + COLUMN_TITLE + " RLIKE " + "?" + ";");
        findBooksByAuthorStmt = con.prepareStatement("SELECT " + COLUMN_BOOK_ID+ ", "+ COLUMN_ISBN + ", " + COLUMN_TITLE + " FROM " + TABLE_BOOK + " WHERE " + COLUMN_BOOK_ID + " IN "
                                                    + "(SELECT " + COLUMN_BOOK_ID + " FROM " + TABLE_BOOK_AUTHOR + " WHERE " + COLUMN_AUTHOR_ID + " IN " +
                                                    "(SELECT " + COLUMN_AUTHOR_ID + " FROM " + TABLE_AUTHOR + " WHERE " + COLUMN_FIRST_NAME + " RLIKE " + "?" + " OR " + COLUMN_LAST_NAME + " RLIKE " + "?" + "))" + ";");

        findBooksByGenreStmt = con.prepareStatement( "SELECT " + COLUMN_BOOK_ID+ ", "+ COLUMN_ISBN + ", " + COLUMN_TITLE + " FROM " + TABLE_BOOK + " WHERE " + COLUMN_BOOK_ID + " IN "
                                + "(SELECT " + COLUMN_BOOK_ID + " FROM " + TABLE_BOOK_GENRE + " WHERE " + COLUMN_GENRE_ID + " IN " +
                                "(SELECT " + COLUMN_GENRE_ID + " FROM " + TABLE_GENRE + " WHERE " + COLUMN_GENRE_TYPE + " RLIKE " + "?" +" ))" + ";");
        createNewBookStmt = con.prepareStatement("INSERT INTO " + TABLE_BOOK +" (" + COLUMN_ISBN + ", " + COLUMN_TITLE + " )" + " VALUES " + "( " + "?" + ", " + "?" + " )" + ";", PreparedStatement.RETURN_GENERATED_KEYS);
        insertAuthorToBookStmt = con.prepareStatement("INSERT INTO " + TABLE_BOOK_AUTHOR + " (" + COLUMN_BOOK_ID + ", " + COLUMN_AUTHOR_ID + ") " + "VALUES " + "(( "+ "SELECT " + COLUMN_BOOK_ID + " FROM " + TABLE_BOOK + " WHERE " + COLUMN_ISBN + " = " + "?" + ")" + ", " + "?" + " )" + ";");
        insertGenreToBookStmt = con.prepareStatement("INSERT INTO " + TABLE_BOOK_GENRE + " (" + COLUMN_BOOK_ID + ", " + COLUMN_GENRE_ID + ") " + "VALUES " + "(( "+ "SELECT " + COLUMN_BOOK_ID + " FROM " + TABLE_BOOK + " WHERE " + COLUMN_ISBN + " = " + "?" + ")" + ", " +"?" +" )" + ";");
        createNewGenreStmt = con.prepareStatement("INSERT INTO " + TABLE_GENRE +  " (" + COLUMN_GENRE_TYPE + ") " + "VALUES " + "(" + "?" + ")" + ";");
        findAllGenresStmt = con.prepareStatement("SELECT " + COLUMN_GENRE_ID + ", " + COLUMN_GENRE_TYPE + " FROM " + TABLE_GENRE + ";");
        createNewAuthorStmt = con.prepareStatement("INSERT INTO " + TABLE_AUTHOR +  " (" + COLUMN_FIRST_NAME + ", " + COLUMN_LAST_NAME + ", " + COLUMN_DOB + " ) " + " VALUES " + " (" + "?" +"," + "?" + ", " + "?" + ")" + ";");
        createNewRatingToBookStmt = con.prepareStatement("INSERT INTO " + TABLE_RATING + " (" + COLUMN_BOOK_ID + ", " + COLUMN_RATING_SCORE + ") " + "VALUES " + "(" + "( "+ "SELECT " + COLUMN_BOOK_ID + " FROM " + TABLE_BOOK + " WHERE " + COLUMN_ISBN + " = " + "?" + ")" + ", " + "?" + ")" + ";");
        checkIfRatingExistsStmt = con.prepareStatement(  "SELECT EXISTS (" + " SELECT " + COLUMN_BOOK_ID + " FROM "  + TABLE_RATING + " WHERE " + COLUMN_BOOK_ID +" IN " + " ("+"SELECT " + COLUMN_BOOK_ID + " FROM " + TABLE_BOOK + " WHERE " + COLUMN_ISBN + " = " + "?" +"))" + ";");
        updateRatingScoreStmt = con.prepareStatement("UPDATE " + TABLE_RATING +  " SET "  + COLUMN_RATING_SCORE +  " = " + "?" +  " WHERE " + COLUMN_BOOK_ID + " IN " + " ("+"SELECT " + COLUMN_BOOK_ID + " FROM " + TABLE_BOOK + " WHERE " + COLUMN_ISBN + " = " + "?" +" )"+ " ;" );
        findAllBooksStmt = con.prepareStatement("SELECT " + COLUMN_BOOK_ID + ", " + COLUMN_ISBN + ", " + COLUMN_TITLE + " FROM " + TABLE_BOOK + ";");
        findBooksByISBNStmt = con.prepareStatement("SELECT " + COLUMN_BOOK_ID + ", " + COLUMN_ISBN + ", " + COLUMN_TITLE + " FROM " + TABLE_BOOK + " WHERE " + COLUMN_ISBN + " RLIKE " + "?" + ";");
        findBooksByRatingStmt = con.prepareStatement("SELECT " +COLUMN_BOOK_ID+ ", " +COLUMN_ISBN+ ", " +COLUMN_TITLE+ " FROM "  + TABLE_BOOK + " WHERE " +COLUMN_BOOK_ID+ " IN " + "(SELECT " +COLUMN_BOOK_ID+ " FROM " + TABLE_RATING + " WHERE " +COLUMN_RATING_SCORE + " = "+ "?" +");");


    }
    void handleException (String errorMessage, Exception cause) throws LibraryDBException {
        try{
            con.rollback();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        if(cause != null)
        throw new LibraryDBException(errorMessage, cause);
        else {
            throw new LibraryDBException(errorMessage);
        }

    }
    void closeResultSet(ResultSet result) throws LibraryDBException {
        try{
            result.close();
        }catch(SQLException e){
            throw new LibraryDBException("could not close resultset", e);
        }
    }
}
