package sample;

import javafx.scene.image.Image;
import javafx.scene.media.*;
import javafx.scene.control.Button;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {
    @FXML
    private MediaView mediaV;

    @FXML
    private Button playPauseButton;

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
        // Build the path to the location of the media file
        String path = new File("src/sample/media/a lot.mp3").getAbsolutePath();
        // Create new Media object (the actual media content)
        me = new Media(new File(path).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        //
        mediaV.setMediaPlayer(mp);
        // mp.setAutoPlay(true);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        mp.setAutoPlay(false);

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


    private void showAlbumCover(File songName){


        // TODO GET THE ALBUM ARTWORK AND MAKE IT DISPLAY IN THE IMAGEVIEW


    }

}
