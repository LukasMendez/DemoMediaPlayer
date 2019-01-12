package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setResizable(true); // may wanna change this
        Parent root = FXMLLoader.load(getClass().getResource("sampleTest.fxml"));
        primaryStage.setTitle("MediaPlayerDemo");
        primaryStage.setScene(new Scene(root, 1080, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);

    }
}
