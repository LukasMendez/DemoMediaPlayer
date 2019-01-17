package sample;

import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import playlistManagement.ExistingPlaylist;
import playlistManagement.Library;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * Controller for the primary stage.
 * A Media Player, where the user can play songs. These songs can be added manually to the Library,
 * and the user can create Playlists that contains songs chosen by the user.
 * It is also possible to remove both songs and playlists
 * from the program.
 *
 * @author Lukas, Pierre, Alexander and Allan.
 *
 *
 */


public class Controller implements Initializable {


    @FXML
    private MediaView mediaV;


    @FXML
    private Label headlineLabel;

    @FXML
    private TextField searchBar;



    // DISPLAYING PLAYLISTS AND SONGS
    @FXML
    private TableView<Song> defaultTableView;

    // 1) Song Title

    @FXML
    private TableColumn<Song, String> songTitleColumn;

    // 2) Song artist

    @FXML
    private TableColumn<Song, String>  songArtistColumn;

    // 3) Song album
    @FXML
    private TableColumn<Song, String>  songAlbumColumn;


    // DURATION OF THE SONG

    @FXML
    private Label displayCurrentTime;

    @FXML
    private Label displayTotalDuration;

    @FXML
    private Slider durationSlider;


    // DISPLAYING META DATA

    @FXML
    private ImageView albumCoverView;

    @FXML
    private Text scrollingText;



    // ADJUSTING THE VOLUME

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button buttonMuteandOn;


    // SONG CONTROL

    @FXML
    private Button playPauseButton;


    // PLAYLIST CONFIGURATION

    @FXML
    private Button addSongButton;

    @FXML
    private Button deleteSongButton;

    @FXML
    private Button deletePlaylistButton;


    // PLAYLIST SIDE PANEL
   @FXML
   private ListView<String> allPlaylistsListView;


    // NON-FXML VARIABLES


    private MediaPlayer mp;
    private Media me;

    private boolean libraryVisible = false;

    private ExistingPlaylist allPlaylists;

    private ArrayList<Song> currentPlayQueue;

    private ArrayList<Song> temporaryPlayQueue;

    private int currentQueueNumberIndex;


    // USED FOR FETCHING THE ALBUM COVER
    private MapChangeListener<String,Object> listener;

    // THE SONG ITSELF
    private Song mySong;

    // USED FOR LOADING DATA FROM THE LIBRARY
    private Library library;



    // WILL BE GIVEN TO AddSongController CLASS

    public static String getTempPlaylistName() {
        return tempPlaylistName;
    }

    // PLAYLIST NAMES - DEFAULT: LIBRARY - AS THIS IS WHERE YOU START

    // This one is the one that you just clicked on. (The one that you are looking at)
    private static String tempPlaylistName = "Library";

    // This one is the one that your music comes from
    private String listeningPlaylistName = "Library";

    // BOOLEANS

    private boolean isFirstStartUp = false; // Will check if its the first time / If there's anything in the library

    private boolean isPlaying = false; // Will check if the music player is playing

    private boolean isMute = false; // Will check if the music is muted or not

    // WILL SET TO TRUE WHENEVER YOU PRESS A NEW PLAYLIST
    private boolean selectedNewPlaylist = false;

    // WILL DETERMINE IF SONGS WILL BE SELECTED IN THE TABLE VIEW WHEN YOU SKIP A SONG
    private boolean showSelectedItems = true;


    // THIS STATIC CONTROLLER CLASS IS CREATED AND USED AS A REFERENCE FROM OTHER CONTROLLER CLASSES
    public static Controller ControllerClass;


    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources) {


        addSongButton.setVisible(false);

        deleteSongButton.setVisible(false);

        deletePlaylistButton.setVisible(false);


        // MADE TO MAKE ACCESS BETWEEN CONTROLLERS POSSIBLE - Will make other classes able to borrow methods from this one
        ControllerClass = this;


        // CALLING THE DEFAULT CONSTRUCTOR. MEANING THAT RIGHT NOW NO PLAYLIST IS SELECTED
        allPlaylists = new ExistingPlaylist();

        // WILL DISPLAY ALL THE PLAYLISTS ON THE SIDE PANEL
        displayAllPlaylist(allPlaylistsListView);



        Library defaultLibrary = new Library();

        defaultLibrary.retrieveAllSongs();

        if (defaultLibrary.getSongsFoundArrayList().size() >= 1) {
            isFirstStartUp = false;
            System.out.println("Not the first time you run the application");
            startUp();
        }else{
            isFirstStartUp = true;
            System.out.println("First Startup Reg.");
        }

        showLibrarySongs();


    }



    //////////////////////////////////////////////////////////
    // MUSIC CONTROL - PLAY/PAUSE - STOP - PREVIOUS/FORWARD //
    //////////////////////////////////////////////////////////

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

            // Whenever you press the button it will re-apply the default settings (you can't just change one style directly)
            // But the main purpose is to change the icon to a pause button
            playPauseButton.setStyle("-fx-background-size: 64px 64px; " + "-fx-background-color:  #1C1C1C;" + "-fx-background-image: url('" + pathPause + "'); ");

            System.out.println("User pressed the play button");

            isPlaying=true;


            // METHOD THAT MANAGES THE DURATION AND MAKES THE USER ABLE TO FAST FORWARD FOR EXAMPLE
            durationManagement();





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

    }


    /**
     * Will play the next song in the queue
     */
    @FXML
    private void playNextSong(){


        try{

            isPlaying=false;

            System.out.println(currentPlayQueue.get(currentQueueNumberIndex+1).getFileName());

            currentQueueNumberIndex++;

            mp.stop();

            setCurrentSong(currentPlayQueue.get(currentQueueNumberIndex));

            handlePlay();

         //   String nameOfCurrentSelected = allPlaylistsListView.getSelectionModel().getSelectedItem();

            System.out.println("Selected mode is on: " + showSelectedItems);


            // WILL CHECK IF IT SHOULD ENTER SELECT MODE


            if (showSelectedItems){


                defaultTableView.getSelectionModel().select(currentQueueNumberIndex);

            }



        } catch (Exception e){


            indexOutOfBoundsHandler();

        }




    }


    /**
     * Will play the previous song from the queue
     */
    @FXML
    private void playPreviousSong(){


        try{

            isPlaying=false;

            System.out.println(currentPlayQueue.get(currentQueueNumberIndex-1).getFileName());

            currentQueueNumberIndex--;

            mp.stop();

            setCurrentSong(currentPlayQueue.get(currentQueueNumberIndex));

            handlePlay();

            defaultTableView.getSelectionModel().select(currentQueueNumberIndex);



        } catch (Exception e){


            indexOutOfBoundsHandler();

        }




    }

    /**
     * Will handle a situation where you try to skip or go back to a song that is out of range.
     * The method resets the index number back to zero and stops the music from playing.
     */

    private void indexOutOfBoundsHandler(){


        System.out.println("Index out of bounds. Resetting the current queue number");

        // WILL SET THE QUEUE INDEX BACK TO ZERO
        currentQueueNumberIndex = 0;

        mp.stop();

        setCurrentSong(currentPlayQueue.get(currentQueueNumberIndex));

        isPlaying=true;

        handlePlay();

        defaultTableView.getSelectionModel().select(0);



    }




    ////////////////////////////////
    // ADDING SONGS AND PLAYLIST //
    ////////////////////////////////

    /**
     * This method will open a popup window using FileChooser and let the user select a file in the format MP3 or WAV.
     * The file will be copied to a specified location, which in this case is the media folder in our sample folder.
     * Besides that, it also scans the media-file for meta data and saves title name, artist name and album name in
     * the database.
     *
     */

    @FXML
    private void addSongToLibrary(){


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

                // System.out.println(file.getName()); DEBUGGING

               mySong = new Song(file.getName());

               // FIRST IT WILL JUST SET THE TITLE TO THE FILENAME AND PRESUME THAT THERE'S NO META DATA

                mySong.setSongTitle(file.getName().substring(0,file.getName().length()-4));

                mySong.setSongArtist("Unknown artist");

                mySong.setSongAlbum("Unknown album");

                // MODIFIED DB TO CHECK IF THERE'S PENDING DATA, IF THIS IS TRUE IT WILL CLEAR THE BUFFER
                if (DB.hasPendingData()){

                    emptyData();
                }

                DB.insertSQL("insert into tblSong VALUES('" + file.getName() +  "','" + mySong.getSongTitle() + "', '" + mySong.getSongArtist() + "','" + mySong.getSongAlbum()+"')");


                // NEXT IT WILL ACTUALLY SEARCH FOR METADATA AND THEN UPDATE THE SQL TABLE IF IT FINDS SOMETHING

               mySong.getMedia().getMetadata().addListener(new MapChangeListener<String,Object>() {
                    @Override
                    public void onChanged(Change<? extends String, ? extends Object> ch) {

                        boolean doneSearching = false;

                        if (ch.wasAdded()) {

                            String key = ch.getKey();
                            Object value = ch.getValueAdded();

                            System.out.println(value);

                            switch (key) {
                                case "album":

                                    mySong.setSongAlbum(value.toString());
                                  //  System.out.println(value.toString());
                                    break;
                                case "artist":
                                    mySong.setSongArtist(value.toString());
                                  //  System.out.println(value.toString());
                                    break;
                                case "title":
                                    mySong.setSongTitle(value.toString());
                                  //  System.out.println(value.toString());
                                    doneSearching = true;
                                    break;


                            }

                            if (doneSearching){


                                // WILL UPDATE THE EXISTING RECORD IN SQL
                                DB.insertSQL("update tblSong set fldTitle='"+mySong.getSongTitle()+"', fldArtist='"+mySong.getSongArtist()+"', fldAlbum='"+mySong.getSongAlbum()+"' where fldName='"+mySong.getFileName()+"'");

                                System.out.println("The data was added to the data base: " + file.getName() + ", " + mySong.getSongTitle() + ", "+ mySong.getSongArtist() + ", " + mySong.getSongAlbum());
                            }
                        }
                    }


                });


            }catch (Exception e){

                System.out.println("There was an error trying to import the file");
                System.out.println(e.getMessage());

                // e.printStackTrace();

            }


            if (libraryVisible){

                // WILL DISPLAY THE LIBRARY RIGHT AFTER YOU IMPORTED THE FILE, SO THAT THE USER KNOWS ITS THERE
                showLibrarySongs();

            }

        }

        // WILL CHECK IF ITS THE FIRST TIME YOU ADD A SONG
        if(isFirstStartUp){
            System.out.println("First Startup getting initialized");
            startUp();
        }


    }


    /**
     * @param text the Text object that we would like to move around
     * @param propoportionElement is a limited parameter since it requires a Slider to align with.
     * In our example we hold the text up against the slider according to length and position.
     */
    private void translateText(Text text, Slider propoportionElement, String songNameText){

        // First we choose the song name we want to put on the Text
        text.setText(songNameText);

        // Algorithm to calculate a reasonable time based on the text length
        int timeInMilis = text.getText().length()*150;

        // Here we define the timing, which in other words is the speed. The higher the number, the slower the speed.
        TranslateTransition tt = new TranslateTransition(Duration.millis(timeInMilis), text);

        // 80 is just an assumption. This will make sure that the text has already appeared before it reaches the interval
        tt.setFromX(0-(text.getText().length()*2));
        tt.setToX(propoportionElement.getPrefWidth()+text.getText().length()+100); /* 100 is the amount of pixels there
        is between the slider and the border, and the proportionElement width is the length of the durationSlider itself.
        We add the length of the text to make all of the text get out of the interval before it shows up again */

        tt.setCycleCount(Timeline.INDEFINITE); // repeats for ever
        tt.setAutoReverse(false); //Do not reverse, always start over

        tt.play();





    }



    /////////////////////////////////////
    // DURATION SLIDER AND TEXT LABELS //
    /////////////////////////////////////

    /**
     * This method gets the duration from the current media, formats it into readable time.
     * Using a listener we can monitor the media player second by second to see how much time has passed and even use the scroller
     * to fast forward (and reverse of course)
     *
     */

    private void durationManagement(){

        // DURATION MANAGEMENT
        // Will listen to the slider and update it continuously
        mp.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov)
            {
                updatesValues();
            }
        });

        // Inorder to jump to the certain part of the media
       // Will change the values if the user moves the slider to a new position
        durationSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov)
            {
                if (durationSlider.isPressed()) { // It would set the time


                    // as specified by user by pressing
                    mp.seek(mp.getMedia().getDuration().multiply(durationSlider.getValue() / 100));
                }
            }
        });





    }


    /**
     *  Method to update the current time of the playing song. This is visualized on a label, so that the user can see the amount
     *  of time that has passed.
     */
    private void updatesValues() {


        mp.currentTimeProperty().addListener((observable, oldTime, newTime) -> {

            if(mp.getCurrentTime().toMillis()>=mp.getTotalDuration().toMillis()-50){
                //System.out.println("CurrentTime: "+mp.getCurrentTime()+" Total Duration: "+mp.getTotalDuration());
                playNextSong();

            }else{

                // System.out.println(mp.getTotalDuration().toMinutes());

                int t = (int) (100 * mp.getTotalDuration().toMinutes());

                String setTotalDuration = "0:00";
                int hourTotalDuration = t / 3600;
                int minTotalDuration = t / 100;
                int secTotalDuration = 60 * (t % 100);

                if (hourTotalDuration == 0) {
                    if (secTotalDuration < 90) {
                        // System.out.printf("%.2s:00 \n", minTotalDuration);
                        setTotalDuration = String.format("%.2s:00", minTotalDuration);
                    } else if (secTotalDuration < 1000) {
                        // System.out.printf("%.2s:0%.1s \n", minTotalDuration, minTotalDuration);
                        setTotalDuration = String.format("%.2s:0%.1s", minTotalDuration, secTotalDuration);
                    } else {
                        // System.out.printf("%.2s:%.2s \n", minTotalDuration, secTotalDuration);
                        setTotalDuration = String.format("%.2s:%.2s", minTotalDuration, secTotalDuration);
                    }
                } else {
                    if (secTotalDuration < 90) {
                        // System.out.printf("%.2:%.2s:00 \n", hourTotalDuration, minTotalDuration);
                        setTotalDuration = String.format("%.2s%.2s:00", hourTotalDuration, minTotalDuration);
                    } else if (secTotalDuration < 1000) {
                        // System.out.printf("%.2:%.2s:0%.1s \n", hourTotalDuration, minTotalDuration, secTotalDuration);
                        setTotalDuration = String.format("%.2s%.2s:0%.1s", hourTotalDuration, minTotalDuration, secTotalDuration);
                    } else {
                        // System.out.printf("%.2s:%.2s:%.2s \n", hourTotalDuration, minTotalDuration, secTotalDuration);
                        setTotalDuration = String.format("%.2:%.2s:%.2s", hourTotalDuration, minTotalDuration, secTotalDuration);
                    }
                }

                durationSlider.setValue(newTime.toMillis() / mp.getTotalDuration().toMillis() * 100);

                String setCurrentTime = "0:00";
                int timeCurrentTime = (int) (100 * mp.getCurrentTime().toMinutes());

                int hourCurrentTime = timeCurrentTime / 3600;
                int minCurrentTime = timeCurrentTime / 100;
                int secCurrentTime = 60 * (timeCurrentTime % 100);


                if (hourCurrentTime == 0) {
                    if (secCurrentTime < 90) {
                        // System.out.printf("%.2s:00 \n", minCurrentTime);
                        setCurrentTime = String.format("%.2s:00", minCurrentTime);
                    } else if (secCurrentTime < 1000) {
                        // System.out.printf("%.2s:0%.1s \n", minCurrentTime, secCurrentTime);
                        setCurrentTime = String.format("%.2s:0%.1s", minCurrentTime, secCurrentTime);
                    } else {
                        // System.out.printf("%.2s:%.2s \n", minCurrentTime, secCurrentTime);
                        setCurrentTime = String.format("%.2s:%.2s", minCurrentTime, secCurrentTime);
                    }
                } else {
                    if (secCurrentTime < 90) {
                        // System.out.printf("%.2:%.2s:00 \n", hourCurrentTime, minCurrentTime);
                        setCurrentTime = String.format("%.2s%.2s:00", hourCurrentTime, minCurrentTime);
                    } else if (secCurrentTime < 1000) {
                        // System.out.printf("%.2:%.2s:0%.1s \n", hourCurrentTime, minCurrentTime, secCurrentTime);
                        setCurrentTime = String.format("%.2s%.2s:0%.1s", hourCurrentTime, minCurrentTime, secCurrentTime);
                    } else {
                        // System.out.printf("%.2s:%.2s:%.2s \n", hourCurrentTime, minCurrentTime, secCurrentTime);
                        setCurrentTime = String.format("%.2:%.2s:%.2s", hourCurrentTime, minCurrentTime, secCurrentTime);
                    }

                }
                displayTotalDuration.setText(setTotalDuration);
                displayCurrentTime.setText(setCurrentTime);
               // System.out.println("CurrentTime: "+mp.getCurrentTime()+" Total Duration: "+mp.getTotalDuration());

            }

        });



        //System.out.println(mp.getCurrentTime().toMinutes()); // Debug

    }


    /////////////////////////////
    // VOLUME AND MUTE CONTROL //
    /////////////////////////////


    /**
     * Will adjust the volume based on the location of the grabber on the slider.
     * Because of the listener, we are able to change the volume in realtime.
     */

    @FXML
    private void adjustVolume() {
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {

                // WILL MAKE SURE THAT IF THE MUSIC IS MUTED AND THE USER CHANGES THE VOLUME, THE FULL SOUND ICON WILL REAPPEAR
                String pathVolumeIcon = "/sample/buttons/volumeFull.png";

                // WILL CHANGE THE MUTE ICON WHEN YOU ADJUST THE VOLUME
                buttonMuteandOn.setStyle("-fx-background-size: 30px 30px; " + "-fx-background-color:  #1C1C1C;" + "-fx-background-image: url('" + pathVolumeIcon + "'); ");

                mp.setVolume(volumeSlider.getValue() / 100);

                isMute=false;


            }
        }

        );
    }


    /**
     * Will mute and unmute sound, when you press the mute button.
     * This method will also make sure, that the icons change as well.
     *
     */
    @FXML
    public void muteAndOnVolume(){

        if (!isMute) {
            mp.setVolume(0);
            isMute = true;
            String pathMute = "/sample/buttons/lydMuteCross.png";

            buttonMuteandOn.setStyle("-fx-background-size: 30px 30px; " + "-fx-background-color:  #1C1C1C;" + "-fx-background-image: url('" + pathMute + "'); ");

            System.out.println("IS THE SONG MUTED: " + isMute);

        }else{
            mp.setVolume(volumeSlider.getValue() / 100);
            isMute = false;
            String pathVolumeIcon = "/sample/buttons/volumeFull.png";

            buttonMuteandOn.setStyle("-fx-background-size: 30px 30px; " + "-fx-background-color:  #1C1C1C;" + "-fx-background-image: url('" + pathVolumeIcon + "'); ");

            System.out.println("IS THE SONG MUTED: " + isMute);
        }

    }


    ///////////////////////////////
    // CONFIGURE SONG AND QUEUE //
    //////////////////////////////


    /**
     * This method receives an ArrayList containing objects in the format "Song"
     * It will then create and update the current play queue
     * @param playQueueArrayList the ArrayList with the new songs to be played
     */

    private void setPlayQueue(ArrayList playQueueArrayList){

        currentPlayQueue = playQueueArrayList;

        // WILL CHECK THE LOCATION OF THE SONG YOU ARE CURRENTLY LISTENING TO. THIS IS SO THAT IT WONT RESTART THE PLAYLIST
        currentQueueNumberIndex = -1; // -1 is just to make sure that we don't choose an index number that could be in the ArrayList

        for (int i = 0; i < currentPlayQueue.size(); i++) {

            if (currentPlayQueue.get(i).getFileName().equals(mySong.getFileName())){

                currentQueueNumberIndex=i;

            }


        }

        // IF NO SONG IS PLAYING THEN IT WILL JUST BEGIN THE PLAYLIST FROM THE FIRST SONG
        if (currentQueueNumberIndex==-1){

            currentQueueNumberIndex=0;

        }


        System.out.println("Current Index Number is: " + currentQueueNumberIndex);
        System.out.println("Actual song number playing is: " + (currentQueueNumberIndex + 1));


    }


    /**
     * Will set the current song and display all information about the song in the GUI
     * Title, artist and album will be put into the translating text and if there is an album cover, it will be
     * displayed as well.
     * @param currentSong
     */

    private void setCurrentSong(Song currentSong) {


        // WILL MAKE A REFERENCE TO INSTANCE VARIABLE
        mySong = currentSong;

        me = currentSong.getMedia();

        mp = new MediaPlayer(me);

        mediaV.setMediaPlayer(mp);
        // mp.setAutoPlay(true);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        mp.setAutoPlay(false);



        // WILL CONTINUE TO USE THE PREVIOUS VOLUME SETTINGS, EVEN IF THE SONG HAS CHANGED
        if (!isMute){

            mp.setVolume(volumeSlider.getValue() / 100);
        } else {

            mp.setVolume(0);

        }


        // WILL DISPLAY THE ALBUM COVER IF THERE IS ONE
        displayAlbumCover(currentSong.getFileName());

        // WILL DISPLAY THE META DATA IN THE LABEL/TEXT
        translateText(scrollingText, durationSlider,currentSong.getSongTitleFromDB() + " - " + currentSong.getSongArtistFromDB() + " - " + currentSong.getSongAlbumFromDB());

        // WILL PRINT THE RELEVANT META DATA IN THE CONSOLE



        System.out.println("Title of the song is: " + currentSong.getSongTitleFromDB());

        System.out.println("Artist of the song is: " + currentSong.getSongArtistFromDB());

        System.out.println("Album of the song is: " + currentSong.getSongAlbumFromDB());




    }


    ///////////////////////////////////
    // VISUALIZATION OF THE METADATA //
    ///////////////////////////////////


    /**
     * Will fetch metadata from the current song and display the album cover in the ImageView.
     */

    private void displayAlbumCover (String fileName){

        // Will create a new song temporary to retrieve the media
        Song song = new Song(fileName);

        // We will try to search the media for relevant meta-data
        ObservableMap<String,Object> meta_data=song.getMedia().getMetadata();


        // Will start to show a blank CD just in case there's no album cover available
        File file = new File("src/sample/images/blank_cd.jpeg");

        Image image = new Image(file.toURI().toString());

        albumCoverView.setImage(image);



        // We assign the listener instance to a variable. This will search for a metadata containing the value "image"
        // It will then display it in the image view
        listener = (MapChangeListener<String, Object>) ch -> {


            if(ch.wasAdded()){

                String key=ch.getKey();

                Object value=ch.getValueAdded();


                switch(key){
                    case "image":


                        // AS SOON AS THE IMAGE IS FOUND IT WILL PUT THE IMAGE UP IN THE IMAGEVIEW
                        albumCoverView.setImage((Image)value);

                        break;

                }
            }
        };


        // Activate the listener
        meta_data.addListener(listener);
        System.out.println("+ Added listener");



    }

    ////////////////////////////////////
    // DISPLAYING SONGS AND PLAYLISTS //
    ////////////////////////////////////

    /**
     * Will show the library in the TableView. This method is also an obvious choice if you want to refresh the library
     */

    @FXML
    private void showLibrarySongs(){

        // WILL HIDE THE PLAYLIST MANAGEMENT BUTTONS

        addSongButton.setVisible(false);

        deleteSongButton.setVisible(false);

        deletePlaylistButton.setVisible(false);


        ///////////////////////////////////////


        headlineLabel.setText("Library");

        // WILL CHECK IF USER IS IN THE PLAYLIST THAT WE ARE LISTENING TO

        tempPlaylistName="Library";


        // IF THE SELECTED PLAYLIST AND THE ONE YOU SELECTED A SONG FROM MATCHES, IT WILL HIGHLIGHT THE SONG YOU ARE LISTENING TO
        if (tempPlaylistName.equals(listeningPlaylistName)){

            showSelectedItems=true;

        } else {

            showSelectedItems=false;

        }


        System.out.println("You pressed Library");

        // BOOLEANS CHANGED:

        libraryVisible=true;

        // LIBRARY WILL IN THIS CASE BE TREATED AS IF YOU CLICKED ON A NEW PLAYLIST
        selectedNewPlaylist=true;


        library = new Library();

        library.displayAllSongs(defaultTableView,songTitleColumn,songArtistColumn,songAlbumColumn);


        // RESETTING TO AVOID THE ARRAY LIST FROM APPENDING THE OLD QUEUE
        library.resetArrayList();

        library.retrieveAllSongs();

        // WILL SET THE NEW QUEUE TO THE SONGS THAT THE ARRAY LIST CONTAINS
        temporaryPlayQueue = library.getSongsFoundArrayList();

        // DEBUGGING AREA
        if (library.getSongsFoundArrayList().size()>0){

            System.out.println("This the file name of library index 0: " + library.getSongsFoundArrayList().get(0).getFileName() + " - Check if correct");

        }


        System.out.println("The size of temporaryPlayQueue is: " + temporaryPlayQueue.size());


    }


    /**
     * This method runs as soon as the program is launched. It will display a list of all your playlists on the left side.
     * This method is also great to use, if you need to refresh the list of playlists.
     * @param listView the ListView element that you want to display it on.
     */

    public void displayAllPlaylist(ListView listView){


        allPlaylists = new ExistingPlaylist();


        allPlaylists.showAllPlaylists(listView);

    }



    ////////////////////////////////////////////////////
    // SELECTION OF OBJECTS IN LIST VIEW AND TABLE VIEW //
    ////////////////////////////////////////////////////


    /**
     * Will select the current song from the current playlist selected. This also works on the Library.
     * Will update the queue as well.
     */

    @FXML
    private void getSongSelected(){


       try{

           mySong = defaultTableView.getSelectionModel().getSelectedItem();

           // WILL PRINT IT OUT IN THE CONSOLE
           System.out.println("Playing: " + mySong.getFileName());

           // WILL STOP THE MUSIC IF THERE WAS ANY PLAYING
           handleStop();


           setCurrentSong(mySong);


           // SINCE YOU ARE PLAYING A SONG FROM THE PLAYLIST YOU JUST SELECTED THE VALUES WILL BE EQUAL
           listeningPlaylistName=tempPlaylistName;

           System.out.println("1. Song comes from: " + listeningPlaylistName);

           showSelectedItems=true;


           System.out.println("2. Playlist that was selected las time: " + tempPlaylistName);


           if (selectedNewPlaylist){

               System.out.println("Selected new playlist and started playing it");


               for (int i = 0; i < temporaryPlayQueue.size(); i++) {

                   System.out.println("---Play queue to be played: " + temporaryPlayQueue.get(i).getFileName());

               }

               // WILL CONFIGURE A NEW QUEUE GIVEN THAT THE USER SELECTED A NEW PLAYLIST AND A NEW SONG AS WELL
               setPlayQueue(temporaryPlayQueue);

           }


       } catch (Exception e){

           System.out.println(e.getMessage());
           System.out.println("Nothing was selected, couldn't play a song");

       }

    }


    /**
     * Will select the playlist that the user clicks on.
     * Will not set it to the currently playing playlist before the user selects a song as well.
     * This is to avoid situations where the user wants to look at other playlists, while still listening to another one.
     */

    @FXML
    private void getPlaylistSelected(){


        // WILL SHOW THE PLAYLIST MANAGEMENT BUTTONS AGAIN

        addSongButton.setVisible(true);

        deleteSongButton.setVisible(true);

        deletePlaylistButton.setVisible(true);



        selectedNewPlaylist = true;

        String selectedPlaylist = allPlaylistsListView.getSelectionModel().getSelectedItem();

        // WILL GET THE NAME OF THE PLAYLIST YOU JUST PRESSED
        tempPlaylistName=selectedPlaylist;

        // WILL SHOW IT IN THE LABEL
        headlineLabel.setText(selectedPlaylist);


        System.out.println("Name of selected playlist: " + selectedPlaylist);

        ExistingPlaylist existingPlaylist = new ExistingPlaylist();

        existingPlaylist.setPlaylistName(selectedPlaylist);

        existingPlaylist.displayAllSongs(defaultTableView,songTitleColumn,songArtistColumn,songAlbumColumn);

        System.out.println("Array size: " + existingPlaylist.getSongsFoundArrayList().size());


        if (existingPlaylist.getSongsFoundArrayList().size()>0){

            System.out.println("Made a new queue for the new playlist");

            // Will save this queue and make it ready for when you start a new playlist
            temporaryPlayQueue = new ArrayList<>(existingPlaylist.getSongsFoundArrayList());


            System.out.println("--------------------------------");
            System.out.println("User selected playlist with the name: " + tempPlaylistName);

            System.out.println("The one you are currently listening to: " + listeningPlaylistName);

            // WILL CHECK IF USER IS IN THE PLAYLIST THAT WE ARE LISTENING TO
            if (tempPlaylistName.equals(listeningPlaylistName)){


                showSelectedItems=true;


            } else {

                showSelectedItems=false;

            }
        }

    }


    /**
     * Will delete the selected song on the TableView from the playlist that the user selected recently.
     */
    @FXML
    private void deleteSelectedFromPlaylist(){


        // CAN'T BE LIBRARY SINCE LIBRARY ISN'T A PLAYLIST. BUTTON SHOULD BE INVISIBLE THOUGH
        if (!tempPlaylistName.equals("Library")){


            Song song = defaultTableView.getSelectionModel().getSelectedItem();

            DB.deleteSQL("delete from tblPlaylistSongs where fldSongName='"+song.getFileName()+"' and fldPlaylistName='"+tempPlaylistName+"'");

            System.out.println("Deleted " + song.getFileName() + " from playlist name " + tempPlaylistName);

            getPlaylistSelected();

            getSongSelected(); // WILL RESET THE MUSIC


        }

    }

    /**
     * Will delete the selected playlist.
     */

    @FXML
    private void deleteSelectedPlaylist(){


        // WILL NOT EXECUTE THE METHOD UNLESS THERE'S PENDING DATA
        if (DB.hasPendingData()){

            emptyData();

        }

        DB.deleteSQL("DELETE FROM tblPlaylistSongs WHERE fldPlaylistName='"+tempPlaylistName+"';");

        DB.deleteSQL("DELETE FROM tblPlaylist WHERE fldName='"+tempPlaylistName+"';");

        System.out.println("Removal successful");

        // WILL REFRESH THE LIST OF PLAYLIST
        displayAllPlaylist(allPlaylistsListView);

        // WILL GO BACK TO YOUR LIBRARY
        showLibrarySongs();

    }



    //////////////////////////////////////////////////
    // METHODS THAT INTERACT WITH OTHER CONTROLLERS //
    //                AND FXML FILES                //
    /////////////////////////////////////////////////


    /**
     * Static method that utilizes the ControllerClass solution, where you basically can execute methods
     * from this class in other classes.
     *
     */
    public static void doneCreatingPlaylist(){

        ControllerClass.displayAllPlaylist(ControllerClass.allPlaylistsListView);

    }

    /**
     * Will initialize a window, where the user gets to choose a name for his new playlist.
     * @throws IOException
     */
    @FXML
    private void createNewPlaylist() throws IOException {


        PopupController playlistCreationWindow = new PopupController();

        playlistCreationWindow.createPlaylistPopup();


    }

    private Stage stage = new Stage();

    /**
     * This method will open up a new Stage and let the user choose the songs he wants to add for the selected playlist.
     * As long as it's not Library, which obviously isn't a playlist.
     * @throws IOException
     */

    @FXML
    private void addSongsToSelectedPlaylist() throws IOException {


        System.out.println("addSongsToSelectedPlaylist says that tempPlaylistName is: " + tempPlaylistName);


        if (!tempPlaylistName.equals("Library")){


           if (!stage.isShowing()){

               try {
                   FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addSongToPlaylistPopUp.fxml"));
                   Parent root1 = (Parent) fxmlLoader.load();
                   stage = new Stage();



                   stage.initModality(Modality.WINDOW_MODAL);
                   stage.getIcons().add(new Image("sample/images/Music-icon.png"));
                   stage.setResizable(false);
                   stage.setAlwaysOnTop(true);

                   stage.setTitle("Add songs to your playlist");
                   stage.setScene(new Scene(root1));

                   // WILL CHECK WHEN THE WINDOW IS CLOSED AND REFRESH THE WINDOW WITH THE NEWLY ADDED SONGS
                   // EVENT HANDLER:
                   stage.setOnHiding( event -> {System.out.println("Closing Stage"); getPlaylistSelected(); } );




                   stage.showAndWait();





               } catch (Exception e){

                   System.out.println("Couldn't open window");

               }


           }




        } else {

            System.out.println("ERROR: You are trying to add songs to an illegal playlist");


        }




    }


    // MOUSE CLICK EVENTS

    /**
     * Will detect mouse clicks on the tableView from where you select the songs
     */

    @FXML
    private void mouseClick(){

        defaultTableView.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        getSongSelected();

                        handlePlay();




                    }

                }
            }
        });

    }

    /**
     * Will detect mouse clicks on the ListView from where you select the playlists
     */

    @FXML
    private void mouseClickPlaylist() {

        allPlaylistsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        getPlaylistSelected();

                    }
                }
            }
        });

    }


    /**
     * Will load the default settings when you run your application. Also found in the initializer.
     */

    private void startUp() {
        Library defaultLibrary = new Library();

        defaultLibrary.retrieveAllSongs();

        if (defaultLibrary.getSongsFoundArrayList().size() > 0) {

            System.out.println("Song number 1 is: " + defaultLibrary.getSongsFoundArrayList().get(0).getFileName());

            currentPlayQueue = defaultLibrary.getSongsFoundArrayList();


            mySong = new Song(currentPlayQueue.get(0).getFileName());

            me = mySong.getMedia();

            setCurrentSong(mySong);

            mp = new MediaPlayer(me);
            //
            mediaV.setMediaPlayer(mp);
            // mp.setAutoPlay(true);
            // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
            mp.setAutoPlay(false);
        }
        isFirstStartUp = false;
        System.out.println("Startup finished");
    }


    /**
     * Will let the user enter keywords in the search bar (TextField) and then search for chars in the database.
     * The content displayed in the TableView will be given as Object: Songs, so that the user can play them as well.
     * @param event
     */

    @FXML
    private void searchForSongs(ActionEvent event){

        addSongButton.setVisible(false);

        deleteSongButton.setVisible(false);

        deletePlaylistButton.setVisible(false);


        Library library = new Library();

        library.searchForSongs(searchBar,defaultTableView,songTitleColumn,songArtistColumn,songAlbumColumn);

        selectedNewPlaylist=true;

        // WILL CREATE A NEW PLAYLIST QUEUE OF THE SONGS THAT YOU FOUND
        temporaryPlayQueue = new ArrayList<>(library.getSongsFoundArrayList());


        tempPlaylistName="search result";


    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //                              DEBUGGING                                           //
    //                                                                                  //
    //////////////////////////////////////////////////////////////////////////////////////


    /**
     * This method was only made for debugging to see if the program got the metadata that it was told to load.
     * @param event click event
     */
    @FXML
    private void checkMetaDataTester(ActionEvent event){


        setCurrentSong(mySong);


        translateText(scrollingText, durationSlider,mySong.getSongTitleFromDB() + " - " + mySong.getSongArtistFromDB() + " - " + mySong.getSongAlbumFromDB());


        //    System.out.println("Name is: " + mySong.getSongTitle());

        //    System.out.println("Artist is: " + mySong.getSongArtist());

        //    System.out.println("Album is: " + mySong.getSongAlbum());




    }


    // DATA BASE METHODS


    /**
     * This method was made based on the existing functions in the DB Class. The purpose is to empty the buffer, so that you
     * dont have any pending data.
     */

    public static void emptyData(){


        do{
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }

        } while(true);


    }



}

