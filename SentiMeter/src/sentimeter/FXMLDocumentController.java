package sentimeter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
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
    ComboBox mySourceBox;

    @FXML
    RadioButton iRadioButtonDay;
    @FXML
    RadioButton iRadioButtonWeek;
    @FXML
    RadioButton iRadioButtonMonth;

    @FXML
    BarChart iBarChart;
    @FXML
    PieChart iPieChart;
    @FXML
    StackedAreaChart iStackedAreaChart;

    final ToggleGroup iToggleGroup = new ToggleGroup();

    private TwitterAPI myTwitter;

    private Timer myTimer;
    private TimerTask myTimerTask;
    private Boolean myRunnerTrigger = false;

    private Section[] mySections;
    private TemplateGauge myTGauge;
    private double iSentiment;
    private long lastTimerCall;
    private AnimationTimer myAnimationTimer;

    final GaugeBar myGaugeBar = new GaugeBar();

    double iBullishCounter = 1.0D;
    double iBearishCounter = 1.0D;
    int iQuestionsmarkCounter = 1;
    int iExclamationmarkCounter = 1;
    int iDotCounter = 1;

    // Define a bu,llish-relevant set of words.
    List<String> iBullishWords = new ArrayList<>(Arrays.asList(
            "Bull", "Bullish", "Profit", "Safety", "Win",
            "All-In", "High", "Bump", "Bumping", "Pump", "Pumping",
            "Buy", "Up", "Uptrend", "FOMO", "Invest", "Investing"
    ));

    // Define a bu,llish-relevant set of words.
    List<String> iBearishWords = new ArrayList<>(Arrays.asList(
            "Bear", "Bearish", "Risk", "Loss", "Suffering",
            "Sell", "Low", "Drop", "Dropped", "Break",
            "Crash", "Down", "Downtrend"
    ));

    public Group createGaugeGroup() {

        this.myTwitter = new TwitterAPI(
                "pOpe5ybA2BJnD58t7mV1E0gQj",
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

        this.myTGauge.setLayoutX((this.subScene.getLayoutX() + this.subScene.getWidth()) / 4);
        this.myTGauge.setLayoutY(this.subScene.getLayoutY() + this.subScene.getHeight() / 8);

        Group myGaugeBarGroup = new Group(myTGauge);

        this.subScene.setUserAgentStylesheet("StyleSheet.css");

        return myGaugeBarGroup;

    }

    private double calculateSentiment(List<String> inputStatusesList) {

        this.iBullishCounter = 1.0D;
        this.iBearishCounter = 1.0D;

        // Iterate over each status.
        inputStatusesList.forEach((statusElement) -> {

            System.out.println(statusElement);

            // Split the status-element in a String-Array.
            String[] myStatusArray = statusElement.split(" ");

            // Count the questionsmarks and exclamationmarks.
            for (String wordElement : myStatusArray) {
                if (wordElement.contains("?")) {
                    this.iQuestionsmarkCounter++;
                } else if (wordElement.contains("!")) {
                    this.iExclamationmarkCounter++;
                } else if (wordElement.contains(".")) {
                    this.iDotCounter++;
                }
            }

            // Iterate over each bullish-word.
            this.iBullishWords.forEach((bullishWord) -> {

                for (String arrayElement : myStatusArray) {
                    if (arrayElement.toUpperCase().contains(bullishWord.toUpperCase()) == true) {
                        this.iBullishCounter++;
                    }
                }

            });

            // Iterate over each bullish-word.
            this.iBearishWords.forEach((bearishWord) -> {

                for (String arrayElement : myStatusArray) {
                    if (arrayElement.toUpperCase().contains(bearishWord.toUpperCase()) == true) {
                        this.iBearishCounter++;
                    }
                }

            });

        });

        System.out.println("this.iBullishCounter = " + this.iBullishCounter);
        System.out.println("this.iBearishCounter = " + this.iBearishCounter);

        if (this.iBullishCounter > this.iBearishCounter) {

            if ((this.iBullishCounter / this.iBearishCounter) > 10) {
                return (this.iBullishCounter / this.iBearishCounter) / 10;
            } else {
                if ((this.iBullishCounter / this.iBearishCounter) > 100) {
                    return (this.iBullishCounter / this.iBearishCounter) / 100;
                }
                return this.iBullishCounter / this.iBearishCounter;
            }

        } else {

            if ((this.iBearishCounter / this.iBullishCounter) > 10) {
                return (this.iBearishCounter / this.iBullishCounter) / -10;
            } else {
                if ((this.iBearishCounter / this.iBullishCounter) > 100) {
                    return (this.iBearishCounter / this.iBullishCounter) / -100;
                }
                return (this.iBearishCounter / this.iBullishCounter) * -1;
            }

        }

    }

    public void analyze() {

        try {

            this.stopRunner();

            String iTargetName = this.mySourceBox.getSelectionModel().getSelectedItem().toString();

            // Check the source.
            switch (iTargetName) {
                case "CNBC":
                    iTargetName = "cnbc";
                    break;
                case "CNBC - FastMoney":
                    iTargetName = "cnbcfastmoney";
                    break;
                case "Roger Ver":
                    iTargetName = "rogerkver";
                    break;
                case "Bloomberg Crypto":
                    // No joke, they got the username 'crypto'. I'm asking me since they got it ... 
                    iTargetName = "crypto";
                    break;
                case "Deutsche Börse":
                    iTargetName = "deutscheboerse";
                    break;
                default:
                    System.out.println("No source was choosen.");
                    break;
            }

            // Check the timeframe.
            switch (((RadioButton) this.iToggleGroup.getSelectedToggle()).getText()) {
                case "Last 24 Hours":
                    // Calcualte the sentiment.
                    this.iSentiment = this.calculateSentiment(this.myTwitter.getTweetsOfTheDay(iTargetName));
                    break;
                case "This Week":
                    // Calcualte the sentiment.
                    this.iSentiment = this.calculateSentiment(this.myTwitter.getTweetsOfThisWeek(iTargetName));
                    break;
                case "This Month":
                    // Calcualte the sentiment.
                    this.iSentiment = this.calculateSentiment(this.myTwitter.getTweetsOfThisMonth(iTargetName));
                    break;
                default:
                    System.out.println("No timeframe was choosen.");
                    break;
            }

            // StackedArea-chart
            // Dots
            XYChart.Series iAreaDots = new XYChart.Series();
            iAreaDots.getData().add(new XYChart.Data(0, this.iDotCounter / 2));
            iAreaDots.getData().add(new XYChart.Data(100, this.iDotCounter / 2));
            // Exclamationmarks
            XYChart.Series iAreaExclamationmark = new XYChart.Series();
            iAreaExclamationmark.getData().add(new XYChart.Data(0, this.iExclamationmarkCounter));
            iAreaExclamationmark.getData().add(new XYChart.Data(100, this.iExclamationmarkCounter));
            // Questionmarks
            XYChart.Series iAreaQuestionmark = new XYChart.Series();
            iAreaQuestionmark.getData().add(new XYChart.Data(0, this.iQuestionsmarkCounter));
            iAreaQuestionmark.getData().add(new XYChart.Data(100, this.iQuestionsmarkCounter));

            this.iStackedAreaChart.getData().removeAll();
            this.iStackedAreaChart.getData().clear();
            this.iStackedAreaChart.getData().addAll(iAreaDots, iAreaExclamationmark, iAreaQuestionmark);

            // Bar-chart
            // Marks
            XYChart.Series iBarChartExclamationMarks = new XYChart.Series();
            iBarChartExclamationMarks.getData().add(new XYChart.Data("", this.iExclamationmarkCounter * 10));
            XYChart.Series iBarChartQuestionMarks = new XYChart.Series();
            iBarChartQuestionMarks.getData().add(new XYChart.Data("", this.iQuestionsmarkCounter * 10));

            XYChart.Series Placeholder = new XYChart.Series();

            this.iBarChart.getData().removeAll();
            this.iBarChart.getData().clear();
            this.iBarChart.getData().addAll(iBarChartExclamationMarks, iBarChartQuestionMarks);

            // Pie-chart
            // Bull-Bear
            ObservableList<PieChart.Data> pieChartData
                    = FXCollections.observableArrayList(
                            new PieChart.Data("++", this.iBullishCounter),
                            new PieChart.Data("--", this.iBearishCounter)
                    );

            this.iPieChart.getData().removeAll();
            this.iPieChart.getData().clear();
            this.iPieChart.setData(pieChartData);

            // this.myGaugeBar.setValue(new Random().nextInt(100));
            // '.getTweets(...)' will only work with a functional internet-connection.
            // 'stopRunner()' and 'startRunner()' are needed if the user
            // it clicking more than once on the analyze-button.
            // First, cancel the current 'run' of 'myTimer'.
            // stopRunner();
            // Then, start a new 'run'.
            startRunner();

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

        } catch (NumberFormatException exception) {
            System.err.println("sentimeter.FXMLDocumentController.createGaugeGroup()"
                    + exception.getLocalizedMessage());
            System.out.println("sentimeter.FXMLDocumentController.createGaugeGroup()"
                    + exception.getLocalizedMessage());
        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setTextAreas() {
        this.myTwitter.setTextArea(this.newsAlpha);
        this.myTwitter.setTextArea(this.newsBeta);
        this.myTwitter.setTextArea(this.newsGamma);
    }

    // This function will set up the news-bars.
    // Interacting with 'myTimer' which is a global object.
    public void startRunner() throws InterruptedException {

        if (this.myRunnerTrigger == false) {

            this.myTimer = new Timer();

            this.myTimerTask = new TimerTask() {
                @Override
                public void run() {
                    setTextAreas();
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
    public void stopRunner() throws InterruptedException {

        if (this.myTimer == null) {
            //
        } else {
            // Stop timer.
            this.myTimer.cancel();
            this.myTimerTask.cancel();
            this.myRunnerTrigger = false;
            // Stop animation.
            this.myAnimationTimer.stop();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Past in the gauge in the subscene.
        this.subScene.setRoot(this.createGaugeGroup());

        this.mySourceBox.getItems().removeAll(this.mySourceBox.getItems());
        this.mySourceBox.getItems().addAll(
                "CNBC",
                "CNBC - FastMoney",
                "Roger Ver",
                "Deutsche Börse",
                "Bloomberg Crypto");
        this.mySourceBox.getSelectionModel().select(0);

        // Assign the RadioButtons to a ToggleGroup.
        this.iRadioButtonDay.setToggleGroup(this.iToggleGroup);
        this.iRadioButtonWeek.setToggleGroup(this.iToggleGroup);
        this.iRadioButtonMonth.setToggleGroup(this.iToggleGroup);

        this.iToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            this.btnAnalyze.setDisable(false);
        }));

        System.out.println("Controller was initialized.");

    }

}
