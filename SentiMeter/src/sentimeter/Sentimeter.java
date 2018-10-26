package sentimeter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Sentimeter extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        Scene scene = new Scene(root);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest((WindowEvent windowsEvent) -> {
            Platform.exit();
            System.exit(0);
            System.out.println("Stage is closing. Stoping runner. Event: " + windowsEvent);
        });

    }

}
