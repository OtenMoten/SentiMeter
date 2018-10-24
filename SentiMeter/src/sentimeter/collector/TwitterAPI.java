package sentimeter.collector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
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

    static float myGlobalCounter = 0.0F;

    List<Status> iStatusList;
    List<String> iTextList;

    public TwitterAPI(String initialConsumerKey, String initialConsumerSecret, String initialTwitterToken, String initialTwitterSecret) {
        this.consumerKey = initialConsumerKey;
        this.consumerSecret = initialConsumerSecret;
        this.twitterToken = initialTwitterToken;
        this.twitterSecret = initialTwitterSecret;
    }

    public boolean isRunning() {
        if (hasDataTrigger = true) {
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
        alpha.setText(this.iTextList.get(myRandom.nextInt(this.iTextList.size())));
        beta.setText(this.iTextList.get(myRandom.nextInt(this.iTextList.size())));
        gamma.setText(this.iTextList.get(myRandom.nextInt(this.iTextList.size())));
    }

    public List<String> getTweetsMonth(String iTarget, int iCount) {

        // Set up initial configuration.
        TwitterFactory myFactory = new TwitterFactory();
        Twitter myTwitter = myFactory.getInstance();
        // Set up login credentials.
        myTwitter.setOAuthConsumer(this.consumerKey, this.consumerSecret);
        myTwitter.setOAuthAccessToken(new AccessToken(this.twitterToken, this.twitterSecret));
        // Initialize the text-list.
        this.iTextList = new ArrayList<>();

        List<Status> tempStatusList = new ArrayList<>();

        Calendar iDate = Calendar.getInstance();

        for (int i = 1; i < 11; i++) {
            try {
                // Attack the target.
                this.iStatusList = myTwitter.getUserTimeline(iTarget, new Paging(i, iCount));

                System.out.println("Check the #" + i + " page");
                // Convert the status-lines in Strings and
                // them to the text-list as String.
                if (this.iStatusList.size() < Integer.MAX_VALUE) {
                    this.iStatusList.forEach((statusElement) -> {
                        // '.getTxt()' extracts the tweet's status-text from
                        // the status-object. Then, they are added to the text-list.
                        // Additionally, remove the 'RT ' (re-tweet) tag.
                        // System.out.println(statusElement);
                        // Filter out the crypto relevant stuff.
                        if ((statusElement.getText().contains("Bitcoin")
                                || statusElement.getText().contains("BTC")
                                || statusElement.getText().contains("XBT")
                                || statusElement.getText().contains("Crypto")
                                || statusElement.getText().contains("Cryptocurrency")
                                || statusElement.getText().contains("Cryptocurrencies"))
                                && statusElement.getCreatedAt().getMonth() + 1 == iDate.getTime().getMonth() + 1) {
                            tempStatusList.add(statusElement);
                            this.iTextList.add(statusElement.getText());
                        } else {
                            //
                        }
                        if (this.iTextList.size() < 1) {
                            this.iTextList.add("Nothing to see here.");
                        }
                    });
                }
                TwitterAPI.hasDataTrigger = true;
            } catch (TwitterException twitterExc) {
                Logger.getLogger(TwitterAPI.class.getName()).log(Level.SEVERE, null, twitterExc);
                System.err.println("$ twitterExc: " + twitterExc.getErrorMessage());
            }

        }

        for (int i = 0; i < iTextList.size(); i++) {
            System.out.println(iTextList.get(i));
        }

        for (int i = 0; i < tempStatusList.size(); i++) {
            System.out.println(tempStatusList.get(i));
        }

        return this.iTextList;

    }

}
