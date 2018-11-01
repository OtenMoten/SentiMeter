package sentimeter.collector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javafx.scene.control.ProgressBar;
import twitter4j.*;

// The word "Share" is a synonym for a "Tweet" or a "Post" or any other social-media related stuff that is created by a social-media profile.
public class TwitterCollector extends AbstractCollector {

    // A Twitter-object from the "Twitter4J" library.
    Twitter iTwitter;
    // The "Twitter4J" library is using the "Status"-class for saving the shares of a twitter-profile.   
    // The list "iStatusList" is saving Status-objects.
    List<Status> iStatusList;

    public TwitterCollector(Twitter newTwitter, String newTarget, ProgressBar newProgressBar, Calendar newDateLimit) {
        // Assign parameters to inner variables.
        this.iTwitter = newTwitter;
        // "this.iTarget" was inherited from the parent-class.
        this.iTarget = newTarget;
        // "this.iProgressBar" was inherited from the parent-class.
        this.iProgressBar = newProgressBar;
        // "this.iDateLimit" was inherited from the parent-class.
        this.iDateLimit = newDateLimit;
        // Initiate the status list.
        this.iStatusList = new ArrayList<>();
    }

    @Override
    public void downloadTimeline() {
        boolean iTrigger = true;
        for (int iPageIndex = 1; iPageIndex <= 10 && iTrigger; iPageIndex++) {
            this.iProgressBar.setProgress(iPageIndex / 10.0);
            try {
                this.iTwitter.getUserTimeline(this.iTarget, new Paging(iPageIndex, 200)).forEach((statusElement) -> {
                    if (statusElement.getCreatedAt().after(this.iDateLimit.getTime())) {
                        // Add the status to list if it's crypto-related.
                        this.iStatusList.add(statusElement);
                    }
                });
            } catch (TwitterException iException) {
                System.err.println("##ERR## @ " + this.getClass() + " @ Constructor"
                        + "\n Message() = " + iException.getMessage());
            }
        }
        System.out.println("Last element by: " + this.iStatusList.get(this.iStatusList.size() - 1).getCreatedAt());
    }

    @Override
    protected Object call() throws Exception {
        updateMessage("Trying to connect to Twitter ...");
        this.downloadTimeline();
        updateMessage("API-connection active.");
        return true;
    }

    @Override
    public List getTimeline() {
        return this.iStatusList;
    }

    @Override
    public String getType() {
        return "Twitter";
    }

}
