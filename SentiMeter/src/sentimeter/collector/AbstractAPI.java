package sentimeter.collector;

import java.util.List;

public abstract class AbstractAPI {

    // User-related.
    private String iUserKey;
    private String iUserSecret;

    // Platform-related.
    private String iTokenKey;
    private String iTokenSecret;

    // Public operations to download tweets of a certain period of time, from the internet.
    abstract public void downloadTweetsOfTheDay(String inputTarget);

    abstract public void downloadTweetsOfThisWeek(String inputTarget);

    abstract public void downloadTweetsOfThisMonth(String inputTarget);

    // Public operations to handle the tweets.
    abstract public List<String> getTweetsOfTheDay(String inputTarget);

    abstract public List<String> getTweetsOfThisWeek(String inputTarget);

    abstract public List<String> getTweetsOfThisMonth(String inputTarget);

    // Other operations to interact with the class.
    abstract public void buildConnection();

    // Setter: User
    final public void setUserKey(String inputUserKey) {
        this.iUserKey = inputUserKey;
    }

    // Setter: User
    final public void setUserSecret(String inputUserSecret) {
        this.iUserSecret = inputUserSecret;
    }

    // Setter: Token
    final public void setTokenKey(String inputTokenKey) {
        this.iTokenKey = inputTokenKey;
    }

    // Setter: Token
    final public void setTokenSecret(String inputTokenSecret) {
        this.iTokenSecret = inputTokenSecret;
    }

    // Getter: User
    final public String getUserKey() {
        return this.iUserKey;
    }

    // Getter: User
    final public String getUserSecret() {
        return this.iUserSecret;
    }

    // Getter: Token
    final public String getTokenKey() {
        return this.iTokenKey;
    }

    // Getter: Token
    final public String getTokenSecret() {
        return this.iTokenSecret;
    }

}
