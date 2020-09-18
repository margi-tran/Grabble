package margi_tran.grabble;

/**
 *
    UserWithPoints.java
 *
 *  Helper class to move username + points data from database.
 *
 *  @author Margi Tran */

public class UserWithHighscore {
    private String username;
    private String highscore;

    public UserWithHighscore(String username, String highscore) {
        this.username = username;
        this.highscore = highscore;
    }

    public String getUsername() {
        return username;
    }

    public String gethighscore() {
        return highscore;
    }
}
