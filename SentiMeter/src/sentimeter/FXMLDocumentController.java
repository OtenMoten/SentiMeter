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
import sentimeter.tgauge.Section;
import sentimeter.tgauge.TGauge;

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

    private Boolean runnerTrigger = false;

    private static final Random RND = new Random();
    private Section[] sections;
    private TGauge gauge;
    private long lastTimerCall;
    private AnimationTimer timer;

    public Group createGaugeNode() {

        this.myTwitter = new TwitterAPI("",
                "",
                "",
                ""
        );

        sections = new Section[]{
            new Section(-10.0, -5.0, Color.RED),
            new Section(-5.0, 0.0, Color.ORANGE),
            new Section(0.0, 7.5, Color.YELLOW),
            new Section(7.5, 10.0, Color.YELLOWGREEN)
        };
        gauge = new TGauge();
        gauge.setMinValue(-10);
        gauge.setMaxValue(10);
        gauge.setThreshold(10);
        gauge.setSections(sections);
        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double lower = -10.0d;
                double upper = 10.0d;
                if (now > lastTimerCall + 3_000_000_000l) {
                    gauge.setValue(RND.nextDouble() * (upper - lower) + lower);
                    lastTimerCall = now; 
                }
            }
        };

        final GaugeBar gaugeBar = new GaugeBar();

        System.out.println((this.subScene.getLayoutX() + this.subScene.getWidth()) / 2);
        System.out.println((this.subScene.getLayoutY() + this.subScene.getHeight()) / 2);

        gauge.setLayoutX((this.subScene.getLayoutX() + this.subScene.getWidth()) / 4);
        gauge.setLayoutY(this.subScene.getLayoutY() + this.subScene.getHeight() / 8);

        this.btnAnalyze.setOnAction((ActionEvent event) -> {
            try {
                gaugeBar.setValue(new Random().nextInt(100));
                this.myTwitter.getTweets("CNBCFastMoney", 10);
            } catch (NumberFormatException exception) {
                System.err.println("sentimeter.FXMLDocumentController.createGaugeNode()"
                        + exception.getLocalizedMessage());
                System.out.println("sentimeter.FXMLDocumentController.createGaugeNode()"
                        + exception.getLocalizedMessage());
            } catch (Exception exception) {
                System.err.println("sentimeter.FXMLDocumentController.createGaugeNode()"
                        + exception.getLocalizedMessage());
                System.out.println("sentimeter.FXMLDocumentController.createGaugeNode()"
                        + exception.getLocalizedMessage());
            }
            try {
                stopRunner();
                startRunner();
                this.runnerTrigger = true;
            } catch (InterruptedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            setWebViews();
            timer.start();
        });

        Group gaugeBarGroup = new Group(gauge);

        this.subScene.setUserAgentStylesheet("StyleSheet.css");

        return gaugeBarGroup;

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

    public void startRunner() throws InterruptedException {
        if (this.runnerTrigger == false) {
            this.myTimer = new Timer();
            this.myTimerTask = new TimerTask() {
                @Override
                public void run() {
                    setNewsBars();
                }
            };
            this.myTimer = new Timer("myTimerString");
            this.myTimer.scheduleAtFixedRate(myTimerTask, 0L, 1000L);
        }
    }

    public void stopRunner() {
        if (this.myTimer == null) {
            System.out.println("The timer was not initiate yet.");
        } else {
            this.myTimer.cancel();
            this.runnerTrigger = false;
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.subScene.setRoot(this.createGaugeNode());

        System.out.println("sentimeter.FXMLDocumentController.initialize()");

    }

}
