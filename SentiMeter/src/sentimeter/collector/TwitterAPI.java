package sentimeter.collector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterAPI extends AbstractAPI {

    // Creating a constant.
    final int MAX_PAGES = 10;
    final int COUNT_OF_TWEETS = 1000;

    // Set up initial configuration.
    Twitter myTwitter = new TwitterFactory().getInstance();

    // Define list to save the status-text of the tweets.
    List<String> iStatusTextList = new ArrayList<>();

    // Define a crypto-relevant set of words.
    List<String> iCryptoWords = new ArrayList<>(Arrays.asList(
            "Bitcoin",
            "BTC",
            "XBT",
            "Crypto",
            "Cryptocurrency",
            "Cryptocurrencies"
    ));

    // Create a random-generator.
    Random iRandomGenerator = new Random();

    // Constructor
    public TwitterAPI(String inputUserKey, String inputUserSecret, String inputTokenKey, String inputTokenSecret) {
        // Set user's parameters.
        this.setUserKey(inputUserKey);
        this.setUserSecret(inputUserSecret);
        // Set service's parameters.
        this.setTokenKey(inputTokenKey);
        this.setTokenSecret(inputTokenSecret);
    }

    @Override
    public void buildConnection() {

        if (this.myTwitter.getAuthorization().isEnabled() == false) {
            // Set up login credentials.
            this.myTwitter.setOAuthConsumer(this.getUserKey(), this.getUserSecret());
            this.myTwitter.setOAuthAccessToken(new AccessToken(this.getTokenKey(), this.getTokenSecret()));
        } else {

        }

    }

    // Download the tweets per day.
    @Override
    public void downloadTweetsOfTheDay(String iTarget) {

        // Create Calendar-objects and get the instance.
        Calendar iCurrentDate = Calendar.getInstance();
        Calendar iStatusDate = Calendar.getInstance();

        // Reset the statuses-list.
        this.iStatusTextList = new ArrayList<>();

        this.buildConnection();

        // Page-index must start at '1', not '0' as usual.
        for (int iPageIndex = 1; iPageIndex <= this.MAX_PAGES; iPageIndex++) {

            try {

                // Convert the status-lines in Strings and then add them to the text-list as String.
                this.myTwitter.getUserTimeline(iTarget, new Paging(iPageIndex, this.COUNT_OF_TWEETS)).forEach((statusElement) -> {

                    // Transform the date of the actucal status-element to the a 'Calendar'-object.
                    iStatusDate.setTime(statusElement.getCreatedAt());

                    this.iCryptoWords.forEach((cryptoWord) -> {

                        // '.getTxt()' extracts the tweet's status-text from each 'statusElement'. 
                        // Then, each 'statusElement' is added to the String-List.
                        // Filter out the crypto relevant stuff.
                        if ((statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord))
                                // Filter out the actual month.
                                && iStatusDate.get(Calendar.DAY_OF_YEAR) == iCurrentDate.get(Calendar.DAY_OF_YEAR)) {
                            // Extract the status-text of the tweet and add it to the String-List 'iStatusTextList'.
                            this.iStatusTextList.add(statusElement.getText());
                        } else {
                            // DO NOTHING
                        }

                    });

                });

            } catch (TwitterException twitterException) {
                Logger.getLogger(TwitterAPI.class.getName()).log(Level.SEVERE, null, twitterException);
                System.err.println("> > > TwitterException is '" + twitterException.getErrorMessage() + "'.");
            }

        }

    }

    // Download the tweets per day.
    @Override
    public void downloadTweetsOfThisWeek(String iTarget) {

        // Create Calendar-objects and get the instance.
        Calendar iCurrentDate = Calendar.getInstance();
        Calendar iStatusDate = Calendar.getInstance();

        // Reset the statuses-list.
        this.iStatusTextList = new ArrayList<>();

        this.buildConnection();

        // Page-index must start at '1', not '0' as usual.
        for (int iPageIndex = 1; iPageIndex <= this.MAX_PAGES; iPageIndex++) {

            try {

                // Convert the status-lines in Strings and then add them to the text-list as String.
                this.myTwitter.getUserTimeline(iTarget, new Paging(iPageIndex, this.COUNT_OF_TWEETS)).forEach((statusElement) -> {

                    // Transform the date of the actucal status-element to the a 'Calendar'-object.
                    iStatusDate.setTime(statusElement.getCreatedAt());

                    this.iCryptoWords.forEach((cryptoWord) -> {

                        // '.getTxt()' extracts the tweet's status-text from each 'statusElement'. 
                        // Then, each 'statusElement' is added to the String-List.
                        // Filter out the crypto relevant stuff.
                        if ((statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord))
                                // Filter out the actual month.
                                && iStatusDate.get(Calendar.WEEK_OF_YEAR) == iCurrentDate.get(Calendar.WEEK_OF_YEAR)) {
                            // Extract the status-text of the tweet and add it to the String-List 'iStatusTextList'.
                            this.iStatusTextList.add(statusElement.getText());
                        } else {
                            // DO NOTHING
                        }

                    });

                });

            } catch (TwitterException twitterException) {
                Logger.getLogger(TwitterAPI.class.getName()).log(Level.SEVERE, null, twitterException);
                System.err.println("> > > TwitterException is '" + twitterException.getErrorMessage() + "'.");
            }

        }

    }

    // Download the tweets per day.
    @Override
    public void downloadTweetsOfThisMonth(String iTarget) {

        // Create Calendar-objects and get the instance.
        Calendar iCurrentDate = Calendar.getInstance();
        iCurrentDate.add(Calendar.MONTH, 1);
        Calendar iStatusDate = Calendar.getInstance();

        // Reset the statuses-list.
        this.iStatusTextList = new ArrayList<>();

        this.buildConnection();

        // Page-index must start at '1', not '0' as usual.
        for (int iPageIndex = 1; iPageIndex <= this.MAX_PAGES; iPageIndex++) {

            try {

                // Convert the status-lines in Strings and then add them to the text-list as String.
                this.myTwitter.getUserTimeline(iTarget, new Paging(iPageIndex, this.COUNT_OF_TWEETS)).forEach((statusElement) -> {

                    // Transform the date of the actucal status-element to the a 'Calendar'-object.
                    iStatusDate.setTime(statusElement.getCreatedAt());
                    iStatusDate.add(Calendar.MONTH, 1);

                    this.iCryptoWords.forEach((cryptoWord) -> {

                        // '.getTxt()' extracts the tweet's status-text from each 'statusElement'. 
                        // Then, each 'statusElement' is added to the String-List.
                        // Filter out the crypto relevant stuff.
                        if ((statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord)
                                || statusElement.getText().contains(cryptoWord))
                                // Filter out the actual month.
                                && iStatusDate.get(Calendar.MONTH) == iCurrentDate.get(Calendar.MONTH)) {
                            // Extract the status-text of the tweet and add it to the String-List 'iStatusTextList'.
                            this.iStatusTextList.add(statusElement.getText());
                        } else {
                            // DO NOTHING
                        }

                    });

                });

            } catch (TwitterException twitterException) {
                Logger.getLogger(TwitterAPI.class.getName()).log(Level.SEVERE, null, twitterException);
                System.err.println("> > > TwitterException is '" + twitterException.getErrorMessage() + "'.");
            }

        }

    }

    // Get the tweets per day.
    @Override
    public List<String> getTweetsOfTheDay(String inputTarget) {
        this.iStatusTextList = new ArrayList<>();
        this.downloadTweetsOfTheDay(inputTarget);

        return this.iStatusTextList;
    }

    // Get the tweets per week.
    @Override
    public List<String> getTweetsOfThisWeek(String inputTarget) {
        this.iStatusTextList = new ArrayList<>();
        this.downloadTweetsOfThisWeek(inputTarget);

        return this.iStatusTextList;
    }

    // Get the tweets per month.
    @Override
    public List<String> getTweetsOfThisMonth(String inputTarget) {
        this.iStatusTextList = new ArrayList<>();
        this.downloadTweetsOfThisMonth(inputTarget);

        return this.iStatusTextList;
    }

    public void setTextArea(TextArea inputTextArea) {

        this.iRandomGenerator = new Random();

        if (this.iStatusTextList.isEmpty()) {
            this.iStatusTextList.add("Nothing.");
        }

        inputTextArea.setText(this.iStatusTextList.get(this.iRandomGenerator.nextInt(this.iStatusTextList.size())));

    }

}
