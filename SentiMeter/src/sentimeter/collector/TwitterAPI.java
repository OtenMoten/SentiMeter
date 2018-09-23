package sentimeter.collector;

import java.util.ArrayList;
import java.util.List;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAPI {

    public List<String> getCNBCFastMoney() {
        // Initial steps before gathering data.
        ConfigurationBuilder myConfig = new ConfigurationBuilder();
        TwitterFactory myFactory = new TwitterFactory(myConfig.build());
        Twitter myTwitter = myFactory.getInstance();
        String target = "CNBCFastMoney";
        // The class 'Status' is related to the Twitter4J-library.
        List<Status> myList;

        try {
            // Set up the API-data from your Twitter Developmer-account.
            myConfig.setDebugEnabled(true).
                    setOAuthConsumerKey("").
                    setOAuthConsumerSecret("").
                    setOAuthAccessToken("").
                    setOAuthAccessTokenSecret("");

            // Attack the target and save the timeline in the list.
            // Now, the list is full of status-objects.
            myList = myTwitter.getUserTimeline(target);

            // Print the statuses on the console.
            myList.forEach((status) -> {
                System.out.println("@"
                        + status.getUser().getScreenName()
                        + " - " 
                        + status.getText()
                );
            });
            
        } catch (TwitterException exception) {
            System.out.println("Failed to get timeline of 'CNBCFastMoney': "
                    + exception.getLocalizedMessage());
            System.err.println("Failed to get timeline of 'CNBCFastMoney': "
                    + exception.getLocalizedMessage());
            System.exit(-1);
        }
        return new ArrayList<>();
    }

}