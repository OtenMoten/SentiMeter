package sentimeter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.chart.AreaChart;
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

public class FXMLDocumentController implements Initializable {

    @FXML
    SubScene subScene;

    @FXML
    Button iButtonAnalyze;
    @FXML
    Button iButtonReset;

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
    ComboBox iDropdown;

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

    // Stuff for datamanagement.
    private TwitterAPI iTwitterAPI;
    private AbstractCollector iCollector;

    // Stuff for the GUI.
    private NewsBarUpdater iNewsBarUpdater;
    private final ToggleGroup iToggleGroup = new ToggleGroup();

    // Thread- and task-related stuff.
    private ExecutorService iExecuterNewsBar;
    private ExecutorService iExecutorCollector;

    // Stuff for the gauge
    private Section[] iSections;
    private TemplateGauge iTGauge;
    private double iSentiment;
    private final GaugeBar myGaugeBar = new GaugeBar();

    // Stuff for the sentiment analysis.
    private double iBullishCounter;
    private double iBearishCounter;
    private int iQuestionmarksCounter;
    private int iExclamationmarksCounter;
    private int iDotCounter;

    // Define a bu,llish-relevant set of words.
    List<String> iBullishWords = new ArrayList<>(Arrays.asList(
            "Bull", "Bullish", "Buy", "Win"
    ));

    // Define a bu,llish-relevant set of words.
    List<String> iBearishWords = new ArrayList<>(Arrays.asList(
            "Bear", "Bearish", "Sell", "Loss"
    ));

    public Group createGaugeGroup() {

        this.iSections = new Section[]{
            new Section(-10.0, -5.0, Color.RED),
            new Section(-5.0, 0.0, Color.ORANGE),
            new Section(0.0, 5.0, Color.YELLOW),
            new Section(5.0, 10.0, Color.YELLOWGREEN)
        };

        this.iTGauge = new TemplateGauge();
        this.iTGauge.setMinValue(-10);
        this.iTGauge.setMaxValue(10);
        // If the current value of the SentiMeter is higher than 
        // the threshold, then a warning-sign is displayed in the SentiMeter.
        this.iTGauge.setThreshold(5);
        // Apply the Section-array 'iSections' to 'iTGauge'-object.
        this.iTGauge.setSections(this.iSections);

        this.iTGauge.setLayoutX((this.subScene.getLayoutX() + this.subScene.getWidth()) / 5);
        this.iTGauge.setLayoutY(this.subScene.getLayoutY() + this.subScene.getHeight() / 5);

        Group myGaugeBarGroup = new Group(this.iTGauge);

        this.subScene.setUserAgentStylesheet("StyleSheet.css");

        this.iTGauge.setValue(1.0F);
        this.iTGauge.setValue(0.0F);

        return myGaugeBarGroup;

    }

    private double getSentiment(List<String> inputStatusesList) {

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

        System.out.println("Bullish words = " + this.iBullishCounter);
        System.out.println("Bearish words = " + this.iBearishCounter);
        System.out.println("? = " + this.iExclamationmarksCounter);
        System.out.println("! = " + this.iQuestionmarksCounter);

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
        switch (this.iDropdown.getSelectionModel().getSelectedItem().toString()) {

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

    public void initiateConnection() {

        // Set the Twitter login-credentials.
        this.iTwitterAPI = new TwitterAPI(
                "pOpe5ybA2BJnD58t7mV1E0gQj",
                "EdsgWi8NzYMDRsfLeOZutkV69ZRnor2FXFECWX12SLyhMSf4gn",
                "957668170394988544-SzR22oHCmCA42orZQoQZE492XPqHrbi",
                "xDur4tDbbttekvljyvz9feBqQwbgeyTn29kOeo85k5viY"
        );

        //this.iLabel.textProperty().bind(this.iTwitterAPI.messageProperty());
        iExecutorCollector = Executors.newFixedThreadPool(1);
        iExecutorCollector.execute(this.iTwitterAPI);
        iExecutorCollector.shutdown();

    }

    public void analyze() {

        // Deactivate the analyze-button.
        this.iButtonAnalyze.setDisable(true);
        // Deactivate the dropdown menu.
        this.iDropdown.setDisable(true);

        Calendar iCalendar = Calendar.getInstance();

        // Check the timeframe.
        switch (((RadioButton) this.iToggleGroup.getSelectedToggle()).getText()) {
            case "Last 24 Hours":
                iCalendar.add(Calendar.DAY_OF_YEAR, -1);
                break;
            case "This Week":
                iCalendar.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case "This Month":
                iCalendar.add(Calendar.MONTH, -1);
                break;
            default:
                System.err.println("No timeframe was choosen.");
                System.err.println("##ERR## @ " + this.getClass() + " @ startGraphics()");

        }

        // Deactivate the togglegroup.
        this.iToggleGroup.getToggles().forEach(toggle -> {
            Node node = (Node) toggle;
            node.setDisable(true);
        });
        // Deactivate the analyze-button because the change of the value in the togglegroup
        // will trigger a listener.
        //this.iButtonAnalyze.setDisable(true);

        // Create a task.
        this.iCollector = new TwitterCollector(this.iTwitterAPI.getTwitter(), getSelectedSource(), this.iProgressBar, iCalendar);

        // Assign a thread to the executors.
        this.iExecutorCollector = Executors.newFixedThreadPool(1);
        this.iExecuterNewsBar = Executors.newFixedThreadPool(1);

        // Set the task's final tasks.
        this.iCollector.setOnSucceeded((succeededEvent) -> {
            this.startGraphics();
            this.iNewsBarUpdater = new NewsBarUpdater(this.iCollector, this.iNewsAlpha, this.iNewsBeta, this.iNewsDelta, this.iNewsEpsilon, this.iNewsIota, this.iNewsKappa);
            this.iButtonReset.setDisable(false);
            this.iDropdown.setDisable(true);
            this.iExecuterNewsBar.execute(this.iNewsBarUpdater);
            this.iExecuterNewsBar.shutdown();
        });

        this.iExecutorCollector.execute(this.iCollector);
        this.iExecutorCollector.shutdown();

    }

    public void startGraphics() {
        List<String> iStatusListText = new ArrayList<>();

        for (int i = 0; i < ((TwitterCollector) this.iCollector).getTimeline().size(); i++) {
            iStatusListText.add(((TwitterCollector) this.iCollector).getTimeline().get(i).toString());
        }

        // Calculate the sentiment.
        this.iSentiment = getSentiment(iStatusListText);

        // StackedArea-chart
        // Dots
        /*
        XYChart.Series iAreaDots = new XYChart.Series();
        iAreaDots.setName(".");
        iAreaDots.getData().add(new XYChart.Data(0, this.iDotCounter / 2));
        iAreaDots.getData().add(new XYChart.Data(100, this.iDotCounter / 2));
         */
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
        this.iStackedAreaChart.getData().addAll(iAreaExclamationmark, iAreaQuestionmark);

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

        this.iTGauge.setValue(iSentiment);

    }

    public void stop() {
        this.iProgressBar.setProgress(0.0D);
        this.iTGauge.setValue(0.0D);
        this.iNewsBarUpdater.stopUpdate();

        this.iDropdown.setDisable(false);
        this.iButtonReset.setDisable(true);
        this.iButtonAnalyze.setDisable(false);
        // Delete the text in the news bars.
        this.iNewsBarUpdater.clear();

        // Deactivate the togglegroup.
        this.iToggleGroup.getToggles().forEach(toggle -> {
            Node node = (Node) toggle;
            node.setDisable(false);
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Initiate the class' variables.
        this.iBullishCounter = 1.0D;
        this.iBearishCounter = 1.0D;
        this.iQuestionmarksCounter = 1;
        this.iExclamationmarksCounter = 1;
        this.iDotCounter = 1;

        // Connect to the Twitter API.
        this.initiateConnection();

        // Past in the gauge in the subscene.
        this.subScene.setRoot(createGaugeGroup());

        this.iDropdown.getItems().removeAll(this.iDropdown.getItems());
        this.iDropdown.getItems().addAll(
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
        this.iDropdown.getSelectionModel().select(0);

        // Assign the RadioButtons to a ToggleGroup.
        this.iRadioButtonDay.setToggleGroup(this.iToggleGroup);
        this.iRadioButtonWeek.setToggleGroup(this.iToggleGroup);
        this.iRadioButtonMonth.setToggleGroup(this.iToggleGroup);

        this.iRadioButtonDay.fire();

    }

}
