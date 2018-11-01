package sentimeter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import sentimeter.api.TwitterAPI;
import sentimeter.collector.AbstractCollector;
import sentimeter.collector.TwitterCollector;
import sentimeter.gaugebar.GaugeBar;
import sentimeter.templategauge.Section;
import sentimeter.templategauge.TemplateGauge;
import twitter4j.Status;

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
    TextArea iNewsAlpha;
    @FXML
    TextArea iNewsBeta;
    @FXML
    TextArea iNewsDelta;
    @FXML
    TextArea iNewsEpsilon;
    @FXML
    TextArea iNewsIota;
    @FXML
    TextArea iNewsKappa;

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
    PieChart iPieChart;
    @FXML
    StackedAreaChart iStackedAreaChart;

    @FXML
    ProgressBar iProgressBar;

    final ToggleGroup iToggleGroup = new ToggleGroup();

    private TwitterAPI iTwitterAPI;

    private AbstractCollector iCollector;

    private Section[] mySections;
    private TemplateGauge myTGauge;
    private double iSentiment;
    private long lastTimerCall;
    private AnimationTimer myAnimationTimer;

    final GaugeBar myGaugeBar = new GaugeBar();

    double iBullishCounter = 1.0D;
    double iBearishCounter = 1.0D;
    int iQuestionmarksCounter = 1;
    int iExclamationmarksCounter = 1;
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

        this.myTGauge.setLayoutX((this.subScene.getLayoutX() + this.subScene.getWidth()) / 5);
        this.myTGauge.setLayoutY(this.subScene.getLayoutY() + this.subScene.getHeight() / 5);

        Group myGaugeBarGroup = new Group(this.myTGauge);

        this.subScene.setUserAgentStylesheet("StyleSheet.css");

        this.myTGauge.setValue(1.0F);
        this.myTGauge.setValue(0.0F);

        return myGaugeBarGroup;

    }

    private double calculateSentiment(List<String> inputStatusesList) {

        this.iBullishCounter = 1.0D;
        this.iBearishCounter = 1.0D;

        // Iterate over each status.
        inputStatusesList.forEach((statusElement) -> {

            // Split the status-element in a String-Array.
            String[] myStatusArray = statusElement.split(" ");

            // Count the questionsmarks and exclamationmarks.
            for (String wordElement : myStatusArray) {
                if (wordElement.contains("?")) {
                    this.iQuestionmarksCounter++;
                } else if (wordElement.contains("!")) {
                    this.iExclamationmarksCounter++;
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

        System.out.println("iBullishCounter = " + this.iBullishCounter);
        System.out.println("iBearishCounter = " + this.iBearishCounter);

        if (this.iBullishCounter >= this.iBearishCounter) {

            if ((this.iBullishCounter / this.iBearishCounter) > 10) {
                return (this.iBullishCounter / this.iBearishCounter) / 10;
            } else if ((this.iBullishCounter / this.iBearishCounter) > 100) {
                return (this.iBullishCounter / this.iBearishCounter) / 100;
            } else if (this.iBullishCounter == 1.0 && this.iBearishCounter == 1.0) {
                return 0.0;
            } else if ((this.iBullishCounter / this.iBearishCounter) < 10) {
                return this.iBullishCounter / this.iBearishCounter;

            } else {
                if ((this.iBearishCounter / this.iBullishCounter) > 10) {
                    return (this.iBearishCounter / this.iBullishCounter) / -10;
                } else if ((this.iBearishCounter / this.iBullishCounter) > 100) {
                    return (this.iBearishCounter / this.iBullishCounter) / -100;
                } else if (this.iBearishCounter == this.iBullishCounter) {
                    return 0.0;
                }
            }
        }

        return (this.iBearishCounter / this.iBullishCounter) * -1;

    }

    public String getSelectedSource() {

        // Check the source.
        switch (this.mySourceBox.getSelectionModel().getSelectedItem().toString()) {

            case "Andreas Antonopoulos":
                return "aantonop";

            case "Bitcoin Magazine":
                return "bitcoinmagazine";

            // Exchanges
            case "Kraken":
                return "krakenfx";

            case "BitMEX":
                return "BitMEXdotcom";

            case "Binance":
                return "binance";

            case "CoinBase":
                return "coinbase";

            case "Bittrex":
                return "BittrexExchange";

            case "Bloomberg Crypto":
                // No joke, they got the username 'crypto'. I'm asking me since they got it ... 
                return "crypto";

            case "Charlie Lee":
                return "satoshilite";

            case "CNBC":
                return "cnbc";

            case "CNBC - FastMoney":
                return "cnbcfastmoney";

            case "CoinDesk":
                return "coindesk";

            case "Cointelegraph":
                return "Cointelegraph";

            case "Deutsche Börse":
                return "deutscheboerse";

            case "John McAfee":
                return "officialmcafee";

            case "Kim Dotcom":
                return "kimdotcom";

            case "Peter Todd":
                return "peterktodd";

            case "Roger Ver":
                return "rogerkver";

            case "Tim Draper":
                return "timdraper";

            case "Vitalik Buterin":
                return "vitalikbuterin";

            default:
                return "";
        }
    }

    public void startGraphics() {

        List<String> iStatusListText = new ArrayList<>();

        for (int i = 0; i < ((TwitterCollector) this.iCollector).getTimeline().size(); i++) {
            iStatusListText.add(((TwitterCollector) this.iCollector).getTimeline().get(i).toString());
        }

        // Calculate the sentiment.
        this.iSentiment = calculateSentiment(iStatusListText);

        // StackedArea-chart
        // Dots
        XYChart.Series iAreaDots = new XYChart.Series();
        iAreaDots.setName(".");
        iAreaDots.getData().add(new XYChart.Data(0, this.iDotCounter / 2));
        iAreaDots.getData().add(new XYChart.Data(100, this.iDotCounter / 2));
        // Exclamationmarks
        XYChart.Series iAreaExclamationmark = new XYChart.Series();
        iAreaExclamationmark.setName("!");
        iAreaExclamationmark.getData().add(new XYChart.Data(0, this.iExclamationmarksCounter));
        iAreaExclamationmark.getData().add(new XYChart.Data(100, this.iExclamationmarksCounter));
        // Questionmarks
        XYChart.Series iAreaQuestionmark = new XYChart.Series();
        iAreaQuestionmark.setName("?");
        iAreaQuestionmark.getData().add(new XYChart.Data(0, this.iQuestionmarksCounter));
        iAreaQuestionmark.getData().add(new XYChart.Data(100, this.iQuestionmarksCounter));

        this.iStackedAreaChart.getData().removeAll();
        this.iStackedAreaChart.getData().clear();
        this.iStackedAreaChart.getData().addAll(iAreaDots, iAreaExclamationmark, iAreaQuestionmark);

        // Pie-chart
        // Bull-Bear
        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList(
                        new PieChart.Data("Bullish", this.iBullishCounter),
                        new PieChart.Data("Bearish", this.iBearishCounter)
                );

        this.iPieChart.getData().removeAll();
        this.iPieChart.getData().clear();
        this.iPieChart.setData(pieChartData);

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

    public void initiateConnection() {

        // Set the Twitter login-credentials.
        this.iTwitterAPI = new TwitterAPI(
                "pOpe5ybA2BJnD58t7mV1E0gQj",
                "EdsgWi8NzYMDRsfLeOZutkV69ZRnor2FXFECWX12SLyhMSf4gn",
                "957668170394988544-SzR22oHCmCA42orZQoQZE492XPqHrbi",
                "xDur4tDbbttekvljyvz9feBqQwbgeyTn29kOeo85k5viY"
        );

        // Set the task's running tasks..
        this.iTwitterAPI.setOnRunning((successesEvent) -> {
            this.btnAnalyze.setDisable(true);
        });

        // Set the task's final tasks..
        this.iTwitterAPI.setOnSucceeded((succeededEvent) -> {
            this.btnAnalyze.setDisable(false);
        });

        //this.iLabel.textProperty().bind(this.iTwitterAPI.messageProperty());
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(this.iTwitterAPI);
        executorService.shutdown();

    }

    public void analyze() {

        Calendar iCalendar = Calendar.getInstance();

        // Check the timeframe.
        switch (((RadioButton) this.iToggleGroup.getSelectedToggle()).getText()) {
            case "Last 24 Hours":
                iCalendar.add(Calendar.DAY_OF_YEAR, -1);
                System.out.println("Calendar: " + iCalendar.get(Calendar.DAY_OF_YEAR));
                break;
            case "This Week":
                iCalendar.add(Calendar.WEEK_OF_YEAR, -1);
                System.out.println("Calendar: " + iCalendar.get(Calendar.WEEK_OF_YEAR));
                break;
            case "This Month":
                iCalendar.add(Calendar.MONTH, -1);
                System.out.println("Calendar: " + iCalendar.get(Calendar.MONTH));
                break;
            default:
                System.err.println("No timeframe was choosen.");
                System.err.println("##ERR## @ " + this.getClass() + " @ startGraphics()");

        }

        this.iCollector = new TwitterCollector(this.iTwitterAPI.getTwitter(), getSelectedSource(), this.iProgressBar, iCalendar);

        // Set the task's final tasks.
        this.iCollector.setOnSucceeded((succeededEvent) -> {
            this.startGraphics();
            this.setTextAreas();
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(this.iCollector);
        executorService.shutdown();

    }

    public void setTextAreas() {
        Random iRandomGen = new Random();
        this.iNewsAlpha.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
        this.iNewsBeta.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
        this.iNewsDelta.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
        this.iNewsEpsilon.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
        this.iNewsIota.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
        this.iNewsKappa.setText(((Status) this.iCollector.getTimeline().get(iRandomGen.nextInt(this.iCollector.getTimeline().size()))).getText());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Connect to the Twitter API.
        this.initiateConnection();

        // Past in the gauge in the subscene.
        this.subScene.setRoot(createGaugeGroup());

        this.mySourceBox.getItems().removeAll(this.mySourceBox.getItems());
        this.mySourceBox.getItems().addAll(
                "Andreas Antonopoulos",
                "Bitcoin Magazine",
                "BitMEX",
                "Binance",
                "Bittrex",
                "Bloomberg Crypto",
                "Charlie Lee",
                "CNBC",
                "CNBC - FastMoney",
                "CoinBase",
                "CoinDesk",
                "Cointelegraph",
                "Deutsche Börse",
                "John McAfee",
                "Kim Dotcom",
                "Kraken",
                "Peter Todd",
                "Roger Ver",
                "Tim Draper",
                "Vitalik Buterin");
        this.mySourceBox.getSelectionModel().select(0);

        // Assign the RadioButtons to a ToggleGroup.
        this.iRadioButtonDay.setToggleGroup(this.iToggleGroup);
        this.iRadioButtonWeek.setToggleGroup(this.iToggleGroup);
        this.iRadioButtonMonth.setToggleGroup(this.iToggleGroup);

        this.iToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            this.btnAnalyze.setDisable(false);
            this.mySourceBox.setDisable(false);
        }));

        System.out.println("Controller was initialized.");

    }

}
