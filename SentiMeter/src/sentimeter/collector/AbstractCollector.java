package sentimeter.collector;

import java.util.Calendar;
import java.util.List;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

// The word "Share" is a synonym for a "Tweet" or a "Post" or any other social-media related stuff that is created by a social-media profile.
public abstract class AbstractCollector extends Task<Object> {

    protected String iTarget;
    protected ProgressBar iProgressBar;
    protected Calendar iDateLimit;

    /**
     * Variable describtion "iListShares"
     *
     * This list contains the shares of a target. It uses a generic type because
     * each Social-Media-API use a different class to represent the shares of a
     * target. As an example, Twitter uses the "Status"-class. At index "0" is
     * the most recent share.
     *
     */
    abstract public void downloadTimeline();

    abstract public List getTimeline();

    abstract public String getType();

}
