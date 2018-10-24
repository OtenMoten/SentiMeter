package sentimeter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import sentimeter.collector.TwitterAPI;
import sentimeter.gaugebar.GaugeBar;
import sentimeter.templategauge.Section;
import sentimeter.templategauge.TemplateGauge;

public class FXMLDocumentController implements Initializable {

    @FXML
    SubScene subScene;

    @FXML
    StackPane myStackPane;

    @FXML
    Button btnAnalyze;

    @FXML
    Button command_One;
    @FXML
    Button command_Two;
    @FXML
    Button command_Three;
    @FXML
    Button command_Four;

    @FXML
    TextArea newsAlpha;
    @FXML
    TextArea newsBeta;
    @FXML
    TextArea newsGamma;

    @FXML
    WebView myWebView;

    @FXML
    ImageView myImageViewAlpha;
    @FXML
    ImageView myImageViewBeta;
    @FXML
    ImageView myImageViewGamma;

    @FXML
    ComboBox mySourceBox;

    private TwitterAPI myTwitter;

    private Timer myTimer;
    private TimerTask myTimerTask;
    private Boolean myRunnerTrigger = false;

    private static final Random RND = new Random();
    private Section[] mySections;
    private TemplateGauge myTGauge;
    private double iSentiment;
    private long lastTimerCall;
    private AnimationTimer myAnimationTimer;

    final GaugeBar myGaugeBar = new GaugeBar();

    public Group createGaugeGroup() {

        this.myTwitter = new TwitterAPI("pOpe5ybA2BJnD58t7mV1E0gQj",
                "EdsgWi8NzYMDRsfLeOZutkV69ZRnor2FXFECWX12SLyhMSf4gn",
                "957668170394988544-SzR22oHCmCA42orZQoQZE492XPqHrbi",
                "xDur4tDbbttekvljyvz9feBqQwbgeyTn29kOeo85k5viY"
        );

        this.mySections = new Section[]{
            new Section(-10.0, -5.0, Color.RED),
            new Section(-5.0, 0.0, Color.ORANGE),
            new Section(0.0, 5.0, Color.YELLOW),
            new Section(5.0, 10.0, Color.YELLOWGREEN)
        };

        this.myTGauge = new TemplateGauge();
        this.myTGauge.setMinValue(-10);
        this.myTGauge.setMaxValue(10);
        // If the current value of the SentiMeter is higher than 
        // the threshold, then a warning-sign is displayed in the SentiMeter.
        this.myTGauge.setThreshold(5);
        // Apply the Section-array 'mySections' to 'myTGauge'-object.
        this.myTGauge.setSections(this.mySections);

        this.lastTimerCall = System.nanoTime();

        System.out.println((this.subScene.getLayoutX() + this.subScene.getWidth()) / 2);
        System.out.println((this.subScene.getLayoutY() + this.subScene.getHeight()) / 2);

        this.myTGauge.setLayoutX((this.subScene.getLayoutX() + this.subScene.getWidth()) / 4);
        this.myTGauge.setLayoutY(this.subScene.getLayoutY() + this.subScene.getHeight() / 8);

        Group myGaugeBarGroup = new Group(myTGauge);

        this.subScene.setUserAgentStylesheet("StyleSheet.css");

        return myGaugeBarGroup;

    }

    private double calculateSentiment(List<String> inputList) {

        double iBullishCounter = 1.0D;
        double iBearishCounter = 1.0D;

        for (String listElement : inputList) {

            if (listElement.toUpperCase().contains("BULL")
                    || listElement.toUpperCase().contains("BULLISH")
                    || listElement.toUpperCase().contains("PROFIT")
                    || listElement.toUpperCase().contains("WIN")
                    || listElement.toUpperCase().contains("ALL-IN")
                    || listElement.toUpperCase().contains("HIGH")
                    || listElement.toUpperCase().contains("BUY")
                    || listElement.toUpperCase().contains("FOMO")) {
                iBullishCounter++;
            } else {
                if (listElement.toUpperCase().contains("BEAR")
                        || listElement.toUpperCase().contains("BEARISH")
                        || listElement.toUpperCase().contains("RISK")
                        || listElement.toUpperCase().contains("LOSS")
                        || listElement.toUpperCase().contains("SELL")
                        || listElement.toUpperCase().contains("LOW")
                        || listElement.toUpperCase().contains("BREAK")
                        || listElement.toUpperCase().contains("CRASH")) {
                    iBearishCounter++;
                }
            }

        }

        System.out.println("iBullishCounter = " + iBullishCounter);
        System.out.println("iBearishCounter = " + iBearishCounter);

        if (iBullishCounter > iBearishCounter) {

            if ((iBullishCounter / iBearishCounter) > 10) {
                return (iBullishCounter / iBearishCounter) / 10;
            } else {
                if ((iBullishCounter / iBearishCounter) > 100) {
                    return (iBullishCounter / iBearishCounter) / 10;
                }
                return iBullishCounter / iBearishCounter;
            }

        } else {

            if ((iBearishCounter / iBullishCounter) > 10) {
                return (iBearishCounter / iBullishCounter) / -10;
            } else {
                if ((iBearishCounter / iBullishCounter) > 100) {
                    return (iBearishCounter / iBullishCounter) / -10;
                }
                return (iBearishCounter / iBullishCounter) * -1;
            }

        }

    }

    public void analyze() {

        List<String> iListOfTweets = new ArrayList<>();

        try {
            this.myGaugeBar.setValue(new Random().nextInt(100));
            // '.getTweets(...)' will only work with a functional internet-connection.
            iListOfTweets = this.myTwitter.getTweetsMonth(
                    "CNBCFastMoney",
                    1000
            );
            // 'stopRunner()' and 'startRunner()' are needed if the user
            // it clicking more than once on the analyze-button.
            // First, cancel the current 'run' of 'myTimer'.
            stopRunner();
            // Then, start a new 'run'.
            startRunner();
        } catch (NumberFormatException | InterruptedException exception) {
            System.err.println("sentimeter.FXMLDocumentController.createGaugeGroup()"
                    + exception.getLocalizedMessage());
            System.out.println("sentimeter.FXMLDocumentController.createGaugeGroup()"
                    + exception.getLocalizedMessage());
        }

        this.setWebViews();

        // Calcualte the sentiment.
        iSentiment = this.calculateSentiment(iListOfTweets);

        // Feed the gauge with values.
        this.myAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double lower = -10.0d;
                double upper = 10.0d;
                if (now > lastTimerCall + 3_000_000_000l) {
                    myTGauge.setValue(iSentiment);
                    //myTGauge.setValue(RND.nextDouble() * (upper - lower) + lower);
                    lastTimerCall = now;
                }
            }
        };

        // Start the gauge.
        this.myAnimationTimer.start();

    }

    public void printTweets() {
        this.myTwitter.printCurrentTweets();
    }

    public void setNewsBars() {
        this.myTwitter.isRunning();
        this.myTwitter.setNewsBars(this.newsAlpha, this.newsBeta, this.newsGamma);
    }

    public void setWebViews() {
        this.myImageViewAlpha.setVisible(true);
        this.myImageViewBeta.setVisible(true);
        this.myImageViewGamma.setVisible(true);
    }

    // This function will set up the news-bars.
    // Interacting with 'myTimer' which is a global object.
    public void startRunner() throws InterruptedException {
        if (this.myRunnerTrigger == false) {
            this.myTimer = new Timer();
            this.myTimerTask = new TimerTask() {
                @Override
                public void run() {
                    setNewsBars();
                }
            };
            this.myTimer = new Timer("myTimerString");
            this.myTimer.scheduleAtFixedRate(this.myTimerTask, 0L, 1000L);
            this.myRunnerTrigger = false;
        }
    }

    // Interacting with 'myTimer' which is a global object.
    // 'myTimer' needs to be canceled with '.cancel()' before a new 
    // 'run' (see 'startRunner()') could be started.
    public void stopRunner() {
        if (this.myTimer == null) {
            //
        } else {
            this.myTimer.cancel();
            this.myRunnerTrigger = false;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Past in the gauge in the subscene.
        this.subScene.setRoot(this.createGaugeGroup());

        this.mySourceBox.getItems().removeAll(this.mySourceBox.getItems());
        this.mySourceBox.getItems().addAll("CNBC - FastMoney", "Source X", "Source Y");
        this.mySourceBox.getSelectionModel().select(0);

        System.out.println("Controller was initialized.");

    }

}
