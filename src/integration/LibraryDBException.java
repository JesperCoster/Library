package integration;

public class LibraryDBException extends Exception {
    public LibraryDBException(String reason){
        super(reason);
    }
    public LibraryDBException(String msg, Exception cause){
        super(msg, cause);
    }
}
