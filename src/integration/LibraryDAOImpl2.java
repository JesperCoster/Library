package integration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import model.Author;
import model.Book;
import model.Genre;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryDAOImpl2 implements LibraryDAO{
    private MongoClient mongoClient;
    private MongoCollection<Document> collection;
    private MongoCollection<Document> collectionAuthor;
    private MongoCollection<Document> collectionGenres;
    MongoCollection<Document> collectionBooks;
    private MongoDatabase database;
    public LibraryDAOImpl2()  {
        connect();
    }
    /**
     * Connects the user to the database using a predetermined string containing where the database is
     * the name of the database and the user and password
     * @throws LibraryDBException
     */
    @Override
    public void connect(){
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost");
        mongoClient = new MongoClient(connectionString);
        database = mongoClient.getDatabase("Library");
        collection = database.getCollection("Books");
        collectionAuthor = database.getCollection("Authors");
        collectionGenres = database.getCollection("Genres");
    }
    /**
     * Closes the connection to the database
     * @throws LibraryDBException
     */
    @Override
    public void disconnect() throws LibraryDBException {
        if(mongoClient != null){
            mongoClient.close();
        }
    }
    /**
     * Creates a new book from the in parameters, and puts it in the db
     * @param isbn Takes in the ISBN of the new book in a String form
     * @param title Takes in the title of the new book in a String form
     * @param authorIds Takes in all id of the authors of the book in form of a int array
     * @param genreIds Takes in all the genre Id:s for the genres of the book, in a int array format
     * @throws LibraryDBException
     */
    @Override
    public void createNewBook(String isbn, String title, int[] authorIds, int[] genreIds) throws LibraryDBException {
        int book_id = 0;
        //finds the appropriate book_id
        Document sort = new Document("book_id", -1);
        FindIterable<Document> highest_id = collection.find().sort(sort).limit(1);
        if(highest_id.first() != null)
        {
            book_id = highest_id.first().getInteger("book_id") + 1;
        }

        int id;
        //Finds the object id for the genre
        Document genreC = collectionGenres.find(new Document("genre_id", genreIds[0])).first();
        List<Document> genres = new ArrayList<>();
        for(int i=0; i<genreIds.length; i++)
        {
            id = genreIds[i];
            Document genre = collectionGenres.find(new Document("genre_id",id)).first();
            Document Genre = new Document()
                    .append("_id", genre.getObjectId("_id"));
            genres.add(Genre);
        }

        //Fins the object id for the authors
        List<Document> authors = new ArrayList<>();
        for(int i=0; i<authorIds.length; i++) {
            id = authorIds[i];
            Document author = collectionAuthor.find(new Document("author_id",id)).first();
            Document Author = new Document()
                    .append("_id", author.getObjectId("_id"));
            authors.add(Author);
        }
        //adds the book to the database
        Document book = new Document()
                .append("book_id", book_id)
                .append("title", title)
                .append("isbn", isbn)
                .append("genres",genres)
                .append("authors", authors);

        collection.insertOne(book);
    }
    /**
     * Puts in a new author in  the database
     * It takes in all attributes for a author and then creates it from these attributes
     * @param firstName takes the first name of the new author
     * @param lastName takes in the last name of the new author
     * @param dob takes in the dob of the new author
     * @throws LibraryDBException
     */
    @Override
    public void createNewAuthor(String firstName, String lastName, LocalDate dob) throws LibraryDBException {
        int author_id = 0;
        Document sort = new Document("author_id", -1);

        //finds the highest author_id
        FindIterable<Document> highest_id = collectionAuthor.find().sort(sort).limit(1);
        if(highest_id.first() != null)
        {
            author_id = highest_id.first().getInteger("author_id") + 1;
        }

        //adds the author to the database
        Document author = new Document()
                .append("author_id", author_id)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("fullName", firstName + " " + lastName)
                .append("dob", dob);

        collectionAuthor.insertOne(author);
    }
    /**
     * Creates a new genre that books could have
     * @param genreName takes in the name of the genre
     * @throws LibraryDBException
     */
    @Override
    public void createNewGenre(String genreName) throws LibraryDBException {
        collectionGenres = database.getCollection("Genres");
        int genre_id = 0;
        Document sort = new Document("genre_id", -1);
        FindIterable<Document> highest_id = collectionGenres.find().sort(sort).limit(1);
        if(highest_id.first() != null)
        {
            genre_id = highest_id.first().getInteger("genre_id") + 1;
        }
        Document genre = new Document()
                .append("genre_id", genre_id)
                .append("genre_type", genreName);
        collectionGenres.insertOne(genre);
    }
    /**
     * Creates a new rating for book
     * @param isbn takes in the ISBN that the new rating is for
     * @param bookRate takes in the new rating that is for the book
     * @throws LibraryDBException
     */
    @Override
    public void createNewRating(String isbn, int bookRate) throws LibraryDBException {
        collectionBooks = database.getCollection("Books");

        Bson filter = Filters.eq("isbn", isbn);
        Bson update = Updates.set("rating",bookRate);
        collectionBooks.updateOne(filter, update);
    }
    /**
     * Searches and finds all the books with a certain title
     * @param title Takes in the title that is going to be searched for
     * @return returns a Arraylist with all the books with a title that matches the in parameter
     * @throws LibraryDBException
     */
    @Override
    public List<Book> searchBooksByTitle(String title) throws LibraryDBException {
        List<Book> bookList = new ArrayList<>();
        FindIterable<Document> books = collection.find(new Document("title", new Document("$regex", title)));

        for (MongoCursor<Document> cursor = books.iterator(); cursor.hasNext();) {
            Document doc = cursor.next();
            bookList.add(new Book(
                    doc.getInteger("book_id"),
                    doc.getString("isbn"),
                    doc.getString("title")));
        }
        return bookList;
    }
    /**
     * Searches and finds all the books with a certain ISBN, could be also be a partial ISBN
     * @param isbn the ISBN that is going to be searched for
     * @return returns a Arraylist with all the books that have a ISBN that matches the in parameter
     * @throws LibraryDBException
     */
    @Override
    public List<Book> searchBooksByIsbn(String isbn) throws LibraryDBException {
        List<Book> bookList = new ArrayList<>();
        FindIterable<Document> books = collection.find(new Document("isbn", new Document("$regex", isbn)));

        for(Document book : books) {
            bookList.add(new Book(1,
                    book.getString("isbn"),
                    book.getString("title")));

        }
        return bookList;
    }
    /**
     * Finds all the books that have been written by a specific author
     * @param name the name of the author, could be the first name, last name or both
     * @return returns a Arraylist of all books that are written by the author
     * @throws LibraryDBException
     */
    @Override
    public List<Book> searchBooksByAuthor(String name) throws LibraryDBException {
        List<Book> bookList = new ArrayList<>();
        Document lookup = new Document("$lookup",
                new Document("from", "Authors")
                        .append("localField", "authors._id")
                        .append("foreignField", "_id")
                        .append("as", "authors")
        );

        Document match = new Document("$match",
                Filters.or(
                        Filters.regex("authors.firstName", name),
                        Filters.regex("authors.lastName", name),
                        Filters.regex("authors.fullName", name)
                )
        );

        AggregateIterable<Document> books = collection.aggregate(
                Arrays.asList(lookup, match)
        );

        for(Document book : books) {
            bookList.add(new Book(
                    book.getInteger("book_id"),
                    book.getString("isbn"),
                    book.getString("title")));

        }
        return bookList;
    }
    /**
     * Finds and returns all the books that have a specific genre
     * @param genreName the name of the genre that is going to be searched for
     * @return returns a Arraylist with all the books that have the genre
     * @throws LibraryDBException
     */
    @Override
    public List<Book> searchBooksByGenre(String genreName) throws LibraryDBException {
        List<Book> bookList = new ArrayList<>();
        Document lookup = new Document("$lookup",
                new Document("from", "Genres")
                        .append("localField", "genres._id")
                        .append("foreignField", "_id")
                        .append("as", "genres")
        );

        Document match = new Document("$match",
                Filters.or(
                        Filters.regex("genres.genre_type", genreName)
                )
        );

        AggregateIterable<Document> books = collection.aggregate(
                Arrays.asList(lookup, match)
        );

        for(Document book : books) {
            bookList.add(new Book(
                    book.getInteger("book_id"),
                    book.getString("isbn"),
                    book.getString("title")));

        }
        return bookList;
    }
    /**
     * Finds all the books in the db with a specific rating
     * @param rating takes the rating that its going to search for in a int form
     * @return returns a Arraylist of all books that have the desired rating
     * @throws LibraryDBException
     */
    @Override
    public List<Book> searchBooksByRating(int rating) throws LibraryDBException {
        List<Book> bookList = new ArrayList<>();
        FindIterable<Document> books = collection.find(new Document("rating", rating));

        for(Document book : books) {
            bookList.add(new Book(1,
                    book.getString("isbn"),
                    book.getString("title")));

        }
        return bookList;
    }
    /**
     * Finds all the authors in the db
     * @return returns a Arraylist with all authors in the db
     * @throws LibraryDBException
     */
    @Override
    public List<Author> findAllAuthors() throws LibraryDBException {
        List<Author> authors = new ArrayList<>();

        collectionAuthor = database.getCollection("Authors");
        FindIterable<Document> authorsDB = collectionAuthor.find();

        for(Document author : authorsDB) {
            authors.add(new Author(author.getInteger("author_id"),
                    author.getString("firstName"),
                    author.getString("lastName"),
                    author.getDate("dob")
            ));
        }
        return authors;
    }
    /**
     * Finds all genres in the db
     * @return returns a Arraylist with all the genres in the db
     * @throws LibraryDBException
     */
    @Override
    public List<Genre> findAllGenres() throws LibraryDBException {
        List<Genre> genres = new ArrayList<>();
        collectionGenres = database.getCollection("Genres");
        FindIterable<Document> genresDB = collectionGenres.find();

        for(Document genre : genresDB) {
            genres.add(new Genre(genre.getInteger("genre_id"),
                    genre.getString("genre_type")));
        }

        return genres;
    }
    /**
     * Finds all books that are in the db
     * @return a Arraylist of all books that are in the db
     * @throws LibraryDBException
     */
    @Override
    public List<Book> findAllBooks() throws LibraryDBException {
        List<Book> books = new ArrayList<>();

        collectionBooks = database.getCollection("Books");
        FindIterable<Document> booksDB = collectionBooks.find();

        for(Document book : booksDB) {
            books.add(new Book(book.getInteger("book_id"),
                    book.getString("isbn"),
                    book.getString("title")));
        }

        return books;
    }
}
