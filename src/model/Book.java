package model;

public class Book implements BookDTO{

    private int bookId;
    private String isbn;
    private String title;

    public Book(int bookId, String isbn, String title){
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
    }

    public int getBookId() {
        return bookId;
    }
    public String getIsbn() {
        return isbn;
    }
    public String getTitle() {
        return title;
    }
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
