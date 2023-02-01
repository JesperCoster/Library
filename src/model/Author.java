package model;

import java.util.Date;

public class Author implements AuthorDTO {
    private int authorId;
    private String firstName;
    private String lastName;
    private Date dob;

    public Author(int authorId, String firstName, String lastName, Date dob) {
        this.authorId = authorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }
    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getAuthorId() {
        return authorId;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public Date getDob() {
        return dob;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return this.firstName +" " + this.lastName;
    }




}
