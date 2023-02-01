package model;

public class Genre implements GenreDTO {
    private int genreId;
    private String genreType;

    public Genre(int genreId, String genreType) {
        this.genreId = genreId;
        this.genreType = genreType;
    }

    public int getGenreId() {
        return genreId;
    }

    public String getGenreType() {
        return genreType;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public void setGenreType(String genreType) {
        this.genreType = genreType;
    }

    @Override
    public String toString() {
        return this.genreType;
    }
}
