package sentimeter;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import sentimeter.gaugebar.GaugeBar;

public class FXMLDocumentController implements Initializable {

    @FXML
    SubScene subScene;

    @FXML
    StackPane myStackPane;

    @FXML
    Button btnAnalyze;

    @FXML
    TextArea newsAlpha;
    @FXML
    TextArea newsBeta;
    @FXML
    TextArea newsGamma;

    public Group createGaugeNode() {

        final GaugeBar gaugeBar = new GaugeBar();

        System.out.println((this.subScene.getLayoutX() + this.subScene.getWidth()) / 2);
        System.out.println((this.subScene.getLayoutY() + this.subScene.getHeight()) / 2);

        gaugeBar.setLayoutX((this.subScene.getLayoutX() + this.subScene.getWidth()) / 4);
        gaugeBar.setLayoutY((this.subScene.getLayoutY() + this.subScene.getHeight()) / 8);

        this.btnAnalyze.setOnAction((ActionEvent event) -> {
            try {
                gaugeBar.setValue(new Random().nextInt(100));
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
        });

        Group result = new Group(gaugeBar);

        this.subScene.setUserAgentStylesheet("StyleSheet.css");

        return result;

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.subScene.setRoot(this.createGaugeNode());

        System.out.println("sentimeter.FXMLDocumentController.initialize()");

    }

}
