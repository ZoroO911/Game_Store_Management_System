package game_store.backend.services;

// Game class
import java.util.Date;

public class Game {
    private int gameID;
    private String title;
    private String genre;
    private double price;
    private Date releaseDate;

    public Game(int gameID, String title, String genre, double price, Date releaseDate) {
        this.gameID = gameID;
        this.title = title;
        this.genre = genre;
        this.price = price;
        this.releaseDate = releaseDate;
    }

    public int getGameID() {
        return gameID;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public double getPrice() {
        return price;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
}
