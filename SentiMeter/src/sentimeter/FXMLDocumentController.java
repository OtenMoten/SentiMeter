package sentimeter;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
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

    private TwitterAPI myTwitter;

    private Timer myTimer;
    private TimerTask myTimerTask;
    private Boolean myRunnerTrigger = false;

    private static final Random RND = new Random();
    private Section[] mySections;
    private TemplateGauge myTGauge;
    private long lastTimerCall;
    private AnimationTimer myAnimationTimer;

    public Group createGaugeGroup() {

        this.myTwitter = new TwitterAPI("pOpe5ybA2BJnD58t7mV1E0gQj",
                "EdsgWi8NzYMDRsfLeOZutkV69ZRnor2FXFECWX12SLyhMSf4gn",
                "957668170394988544-SzR22oHCmCA42orZQoQZE492XPqHrbi",
                "xDur4tDbbttekvljyvz9feBqQwbgeyTn29kOeo85k5viY"
        );

        this.mySections = new Section[]{
            new Section(-10.0, -5.0, Color.RED),
            new Section(-5.0, 0.0, Color.ORANGE),
            new Section(0.0, 7.5, Color.YELLOW),
            new Section(7.5, 10.0, Color.YELLOWGREEN)
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
        this.myAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double lower = -10.0d;
                double upper = 10.0d;
                if (now > lastTimerCall + 3_000_000_000l) {
                    myTGauge.setValue(RND.nextDouble() * (upper - lower) + lower);
                    lastTimerCall = now; 
                }
            }
        };

        final GaugeBar myGaugeBar = new GaugeBar();

        System.out.println((this.subScene.getLayoutX() + this.subScene.getWidth()) / 2);
        System.out.println((this.subScene.getLayoutY() + this.subScene.getHeight()) / 2);

        this.myTGauge.setLayoutX((this.subScene.getLayoutX() + this.subScene.getWidth()) / 4);
        this.myTGauge.setLayoutY(this.subScene.getLayoutY() + this.subScene.getHeight() / 8);

        this.btnAnalyze.setOnAction((ActionEvent event) -> {
            try {
                myGaugeBar.setValue(new Random().nextInt(100));
                // '.getTweets(...)' will only work with a functional internet-connection.
                this.myTwitter.getTweets("CNBCFastMoney", 10);
            } catch (NumberFormatException exception) {
                System.err.println("sentimeter.FXMLDocumentController.createGaugeGroup()"
                        + exception.getLocalizedMessage());
                System.out.println("sentimeter.FXMLDocumentController.createGaugeGroup()"
                        + exception.getLocalizedMessage());
            } catch (Exception exception) {
                System.err.println("sentimeter.FXMLDocumentController.createGaugeGroup()"
                        + exception.getLocalizedMessage());
                System.out.println("sentimeter.FXMLDocumentController.createGaugeGroup()"
                        + exception.getLocalizedMessage());
            }
            try {
                // First, cancel the current 'run' of 'myTimer'.
                stopRunner();
                // Then, start a new 'run'.
                startRunner();
            } catch (InterruptedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            setWebViews();
            this.myAnimationTimer.start();
        });

        Group myGaugeBarGroup = new Group(myTGauge);

        this.subScene.setUserAgentStylesheet("StyleSheet.css");

        return myGaugeBarGroup;

    }

    public void printTweets() {
        this.myTwitter.printCurrentTweets();
    }

    public void setNewsBars() {
        if (this.myTwitter.isRunning()) {
            this.myTwitter.setNewsBars(this.newsAlpha, this.newsBeta, this.newsGamma);
        }
    }

    public void setWebViews() {
        this.myImageViewAlpha.setVisible(true);
        this.myImageViewBeta.setVisible(true);
        this.myImageViewGamma.setVisible(true);
    }

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
            System.out.println("The myAnimationTimer was not initiate yet.");
        } else {
            this.myTimer.cancel();
            this.myRunnerTrigger = false;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.subScene.setRoot(this.createGaugeGroup());

        System.out.println("sentimeter.FXMLDocumentController.initialize()");

    }

}