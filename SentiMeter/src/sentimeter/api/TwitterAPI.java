package sentimeter.api;

import javafx.concurrent.Task;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterAPI extends Task<Object> {

    // Set up initial configuration.
    private final Twitter iTwitter;
    private final String iUserKey;
    private final String iUserSecret;
    private final String iTokenKey;
    private final String iTokenSecret;

    // Constructor
    public TwitterAPI(String newUserKey, String newUserSecret, String newTokenKey, String newTokenSecret) {

        // Set user's parameters.
        this.iUserKey = newUserKey;
        this.iUserSecret = newUserSecret;

        // Set service's parameters.
        this.iTokenKey = newTokenKey;
        this.iTokenSecret = newTokenSecret;

        // Create a TwitterFactory.
        this.iTwitter = new TwitterFactory().getInstance();

    }

    @Override
    protected Object call() throws Exception {
        updateMessage("Trying to connect to Twitter ...");
        this.buildConnection();
        updateMessage("API-connection active.");
        return true;
    }

    public void buildConnection() {
        // Set the Twitter-API credentials if they are NOT(!) set.
        if (!this.iTwitter.getAuthorization().isEnabled()) {
            // Set the login credentials.
            this.iTwitter.setOAuthConsumer(this.iUserKey, this.iUserSecret);
            this.iTwitter.setOAuthAccessToken(new AccessToken(this.iTokenKey, this.iTokenSecret));
            try {
                // Print the profile of the current Twitter-API user.
                System.out.println("Current user: " + this.iTwitter.verifyCredentials().getName());
                System.out.println("URL: " + this.iTwitter.verifyCredentials().getURL());
            } catch (TwitterException iException) {
                System.err.println("##ERR## @ " + this.getClass() + " @ Constructor"
                        + "\n Message() = " + iException.getMessage());
            }
        }
    }

    public Twitter getTwitter() {
        return this.iTwitter;
    }

}
