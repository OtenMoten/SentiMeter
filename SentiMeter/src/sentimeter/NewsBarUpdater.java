package sentimeter;

import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import sentimeter.collector.AbstractCollector;
import twitter4j.Status;

public class NewsBarUpdater extends Task<Object> {

    private final AbstractCollector iCollector;

    private final TextArea iNewsBarAlpha;
    private final TextArea iNewsBarBeta;
    private final TextArea iNewsBarDelta;
    private final TextArea iNewsBarEpsilon;
    private final TextArea iNewsBarIota;
    private final TextArea iNewsBarKappa;

    private long iLastTimerCall;
    private AnimationTimer iAnimationTimer;

    private final Random iRandomGen;

    private boolean isActive;

    public NewsBarUpdater(
            AbstractCollector newTwitterCollector,
            TextArea newNewsBarAlpha,
            TextArea newNewsBarBeta,
            TextArea newNewsBarDelta,
            TextArea newNewsBarEpsilon,
            TextArea newNewsBarIota,
            TextArea newNewsBarKappa) {

        this.iCollector = newTwitterCollector;

        this.iRandomGen = new Random();

        this.iNewsBarAlpha = newNewsBarAlpha;
        this.iNewsBarBeta = newNewsBarBeta;
        this.iNewsBarDelta = newNewsBarDelta;
        this.iNewsBarEpsilon = newNewsBarEpsilon;
        this.iNewsBarIota = newNewsBarIota;
        this.iNewsBarKappa = newNewsBarKappa;

    }

    @Override
    protected Object call() throws Exception {

        updateMessage("Updating news bars ...");

        switch (this.iCollector.getType()) {
            case "Twitter":
                updateTwitter();
                break;
            case "Facebook":
                //updateFacebook();
                break;
            default:
                System.err.println("##ERR## @ " + this.getClass() + " @ Switch-Case 'call()'.");
        }

        updateMessage("News bars updated.");

        return true;

    }

    private void setNewsBars() {
        this.iNewsBarAlpha.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
        this.iNewsBarBeta.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
        this.iNewsBarDelta.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
        this.iNewsBarEpsilon.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
        this.iNewsBarIota.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
        this.iNewsBarKappa.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
    }

    public void clear() {
        this.iNewsBarAlpha.setText("News Feed #1");
        this.iNewsBarBeta.setText("News Feed #2");
        this.iNewsBarDelta.setText("News Feed #3");
        this.iNewsBarEpsilon.setText("News Feed #4");
        this.iNewsBarIota.setText("News Feed #5");
        this.iNewsBarKappa.setText("News Feed #6");
    }

    // Twitter uses the "Status"-class.
    private void updateTwitter() {
        // Get the current system time.
        this.iLastTimerCall = System.nanoTime();
        // Update once the news bars.);
        this.setNewsBars();
        // Activate the running-trigger.
        this.isActive = true;
        this.iAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now > iLastTimerCall + 10_000_000_000l) {
                    setNewsBars();
                    iLastTimerCall = now;
                }
            }
        };
        this.iAnimationTimer.start();
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void stopUpdate() {
        // Only stop the time if the timeline is NOT empty.
        // If It's empty, the time was not startet and '.stop()' will cause a error message during runtime.
        if (!this.iCollector.getTimeline().isEmpty()) {
            this.iAnimationTimer.stop();
            this.isActive = false;
        }
    }

}
