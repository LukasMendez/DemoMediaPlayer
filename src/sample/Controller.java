package sample;

import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.*;
import javafx.scene.control.Button;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

// import java.awt.event.ActionEvent;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {
    @FXML
    private MediaView mediaV;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button addMusicButton;

    @FXML
    private Slider volumeSlider;

    @FXML
    private ImageView albumCoverView;

    @FXML
    private Text scrollingText; // SECOND ATTEMPT FOR SONG BUT AS TEXT

    @FXML
    private Pane bottomPane;


    @FXML
    private Slider durationSlider;

    @FXML
    private Label fileLabel;

    private MediaPlayer mp;
    private Media me;

    private boolean isPlaying = false; // Will check if the music player is playing


    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){


        DB.selectSQL("select * from tblSong"); // Just checking if theres a connection

        // Build the path to the location of the media file
        String path = new File("src/sample/media/Post Malone - Rich & Sad.mp3").getAbsolutePath();
        // Create new Media object (the actual media content)
        me = new Media(new File(path).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        //
        mediaV.setMediaPlayer(mp);
        // mp.setAutoPlay(true);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        mp.setAutoPlay(false);

        // WILL DISPLAY THE ALBUM COVER IF THERE IS ONE
        displayAlbumCover();

        // DISPLAYING THE SONG NAME IN THE GUI DYNAMICALLY
        translateText(scrollingText,durationSlider);


    }


    @FXML
    /**
     * Handler for the play and pause button.
     */
    private void handlePlay()
    {

        if (!isPlaying){


            // Play the mediaPlayer with the attached media
            mp.play();

            String pathPause = "/sample/buttons/pause.png"; // The path has to be relative in order for CSS to find the image and treat it.

            playPauseButton.setStyle("-fx-background-size: 64px 64px; " + "-fx-background-color:  #1C1C1C;" + "-fx-background-image: url('" + pathPause + "'); ");

            System.out.println("User pressed the play button");

            isPlaying=true;


        } else {

            // If the isPlaying boolean is set to true, it will skip the if statement and pause the music

            mp.pause();

            String pathPlay = "/sample/buttons/play.png"; // The path has to be relative in order for CSS to find the image and treat it.

            playPauseButton.setStyle("-fx-background-size: 64px 64px; " + "-fx-background-color:  #1C1C1C;" + "-fx-background-image: url('" + pathPlay + "'); ");


            System.out.println("User pressed the pause button");

            isPlaying=false;

        }



    }

    /**
     * Will stop the music completely
     */

    @FXML
    private void handleStop(){

        System.out.println("User pressed the stop button");

        mp.stop();

        String pathPlay = "/sample/buttons/play.png"; // The path has to be relative in order for CSS to find the image and treat it.

        playPauseButton.setStyle("-fx-background-size: 64px 64px; " + "-fx-background-color:  #1C1C1C;" + "-fx-background-image: url('" + pathPlay + "'); ");


        isPlaying=false;

        Scanner in = new Scanner(System.in);

       // System.out.println("Enter song name: ");

       // String nameOfSong = in.nextLine();

       // changeSong(nameOfSong);


        // TESTING

    }






    @FXML
    private void addSongToLibrary(ActionEvent event){


        Stage stage = new Stage(); // Created in order to trigger the FileChooser dialog

        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Audio files (*.mp3) or (.wav)", "*.mp3","*.wav");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            System.out.println("Path to file: " + file.getPath());

            try{




                Path src = Paths.get(file.getPath()); // fileName is the absolute path.

                Path dest = Paths.get(new File("src/sample/media").getPath()); // folderName is the absolute path.
                Files.copy(src, dest.resolve(src.getFileName()));

            }catch (Exception e){

                System.out.println("There was an error trying to import the file");
                System.out.println(e.getMessage());

                // e.printStackTrace();

            }

        }

    }


    /**
     * Will change the song playing
     * @param filename the name of the song. Will be .mp3 or .wav
     */

    private void changeSong(String filename){

        // Build the path to the location of the media file
        String path = new File("src/sample/media/"+filename).getAbsolutePath();
        // Create new Media object (the actual media content)
        me = new Media(new File(path).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);

        // Will play the selected song
        mp.play();



    }



    /**
     * Will fetch metadata from the current song and display the album cover in the ImageView.
     *
     */

    private void displayAlbumCover (){

        // Will start to show a blank CD
        File file = new File("src/sample/images/blank_cd.jpeg");

        Image image = new Image(file.toURI().toString());

        albumCoverView.setImage(image);


        // However if an album cover is found in the meta-data it will be displayed
        ObservableMap<String,Object> meta_data=me.getMetadata();

        meta_data.addListener((MapChangeListener<String, Object>) ch -> {



            if(ch.wasAdded()){

                String key=ch.getKey();

                Object value=ch.getValueAdded();

                System.out.println(key);

                System.out.println(value);

                switch(key){
                    case "image":
                        albumCoverView.setImage((Image)value);
                        break;
                }
            }
        });

    }


    /**
     *
     * @param text the Text object that we would like to move around
     * @param propoportionElement is a limited parameter since it requires a Slider to align with.
     * In our example we hold the text up against the slider according to length and position.
     */
    private void translateText(Text text, Slider propoportionElement){

        // Here we define the timing, which in other words is the speed. The higher the number, the slower the speed.
        TranslateTransition tt = new TranslateTransition(Duration.millis(4500), text);

        // 80 is just an assumption. This will make sure that the text has already appeared before it reaches the interval
        tt.setFromX(0-text.getText().length()-100);
        tt.setToX(propoportionElement.getPrefWidth()+text.getText().length()); /* PrefWidth = 370, and is the length
        of the durationSlider we add the length of the text to make it disappear before it restarts */

        tt.setCycleCount(Timeline.INDEFINITE); // repeats for ever
        tt.setAutoReverse(false); //Do not reverse, always start over

        tt.play();





    }


    @FXML
    private void adjustVolume() {
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mp.setVolume(volumeSlider.getValue() / 100);
            }
        }

        );
    }


}

