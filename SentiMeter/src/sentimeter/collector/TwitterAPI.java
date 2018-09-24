package sentimeter.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterAPI {

    final String consumerKey;
    final String consumerSecret;
    final String twitterToken;
    final String twitterSecret;

    static int iStaticCount = 0;
    static boolean hasDataTrigger = false;

    List<Status> iStatusList;
    List<String> iTextList;

    public TwitterAPI(String initialConsumerKey, String initialConsumerSecret, String initialTwitterToken, String initialTwitterSecret) {
        this.consumerKey = initialConsumerKey;
        this.consumerSecret = initialConsumerSecret;
        this.twitterToken = initialTwitterToken;
        this.twitterSecret = initialTwitterSecret;
    }

    public boolean isRunning() {
        if(hasDataTrigger = true) {
            return true;
        } else {
            return false;
        }
    }
    
    public void printCurrentTweets() {
        this.iTextList.forEach((listElement) -> {
            System.out.println(listElement);
        });
    }

    public void setNewsBars(TextArea alpha, TextArea beta, TextArea gamma) {
        Random myRandom = new Random();
        alpha.setText(this.iTextList.get(myRandom.nextInt(9)));
        beta.setText(this.iTextList.get(myRandom.nextInt(9)));
        gamma.setText(this.iTextList.get(myRandom.nextInt(9)));
    }

    public void getTweets(String iTarget, int iCount) {
        // Set up initial configuration.
        TwitterFactory myFactory = new TwitterFactory();
        Twitter myTwitter = myFactory.getInstance();
        // Set up login credentials.
        myTwitter.setOAuthConsumer(this.consumerKey, this.consumerSecret);
        myTwitter.setOAuthAccessToken(new AccessToken(this.twitterToken, this.twitterSecret));
        // Initialize the text-list.
        this.iTextList = new ArrayList<>();
        // Set count of tweets.
        int totalTweets = iCount;
        Paging paging = new Paging(1, totalTweets);

        try {
            // Attack the target.
            this.iStatusList = myTwitter.getUserTimeline(iTarget, paging);
            // Convert the status-lines in Strings and
            // them to the text-list as String.
            this.iStatusList.forEach((statusElement) -> {
                //'.getTxt()' extracts the tweet's status-text from
                // the status-object. Then, they are added to the text-list.
                // Additionally, remove the 'RT ' (re-tweet) tag.
                System.out.println(statusElement);
                this.iTextList.add(statusElement.getText().replaceFirst("RT ", ""));
            });
            hasDataTrigger = true;
        } catch (TwitterException twitterExc) {
            Logger.getLogger(TwitterAPI.class.getName()).log(Level.SEVERE, null, twitterExc);
            System.err.println("$ twitterExc: " + twitterExc.getErrorMessage());
        }

    }

}
