package sample;

// HEJ KIM HVORDAN GÅR DET? FINDER DU NOGET DU KAN BRUGE? :P


import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

// import java.awt.event.ActionEvent;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
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

public class Controller implements Initializable {


    @FXML
    private MediaView mediaV;

    @FXML
    private Button addMusicButton;



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



    @FXML
    private ListView<ExistingPlaylist> defaultListView;


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

    @FXML
    private Button testButton;


    // SONG CONTROL

    @FXML
    private Button playPauseButton;


    // PLAYLIST CONFIGURATION


   @FXML
   private ListView<String> allPlaylistsListView;


   @FXML
   private Label fileLabel;


    // NON-FXML VARIABLES


    private MediaPlayer mp;
    private Media me;

    private boolean libraryVisible = false;

    private ExistingPlaylist allPlaylists;

    private ArrayList<Song> currentPlayQueue;

    private ArrayList<Song> temporaryPlayQueue; // TODO CHECK IF NEEDED

    private int currentQueueNumberIndex;


    // USED FOR FETCHING THE ALBUM COVER
    private MapChangeListener<String,Object> listener;

    private Song mySong; // TODO CHANGE THIS


    private Library library;

    private boolean isOnShuffle = false;

    private boolean isPlaying = false; // Will check if the music player is playing

    private boolean isMute = false; // Will check if the music is muted or not

    // TODO CHECK IF NEEDED
    private boolean selectedNewPlaylist = false;

    // TODO CHECK IF NEEDED
    private boolean showSelectedItems = true;

    private String currentPlaylistPlaying;




    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources) {


        allPlaylists = new ExistingPlaylist();




        /////////////////////////////////////////////////////

        // TESTING AREA


     /*   Song song1 = new Song("a lot.mp3");

        Song song2 = new Song("Work Out.mp3");

        Song song3 = new Song("Jesus Walks.mp3");

        Playlist myPlaylist = new Playlist("Shitty music");

        myPlaylist.addSong(song1);

        myPlaylist.addSong(song2); */



        // WILL DISPLAY ALL THE PLAYLISTS ON THE SIDE PANEL
        displayAllPlaylist(allPlaylistsListView);


    //    ExistingPlaylist myPlaylist = new ExistingPlaylist();

      //  myPlaylist.setPlaylistName("Best Mumble Rappers");


        // myPlaylist.displayAllSongs(defaultTableView,songTitleColumn,songArtistColumn,songAlbumColumn);



        //////////////////////////////////////////////////////


        Library defaultLibrary = new Library();

        defaultLibrary.retrieveAllSongs();

        System.out.println("Song number 1 is: " + defaultLibrary.getSongsFoundArrayList().get(0).getFileName());

        currentPlayQueue = defaultLibrary.getSongsFoundArrayList();

        defaultTableView.getSelectionModel().select(0);

        mySong = new Song(currentPlayQueue.get(0).getFileName());

        me = mySong.getMedia();

        // THE ONE WE USE FOR TESTING

        setCurrentSong(mySong);

        mp = new MediaPlayer(me);
        //
        mediaV.setMediaPlayer(mp);
        // mp.setAutoPlay(true);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        mp.setAutoPlay(false);

        showLibrarySongs();


        System.out.println("DEBUGGING - DETAILS ABOUT RECENTLY ADDED SONG: " + mySong.getSongTitle() + ", " + mySong.getSongArtist() + ", " + mySong.getSongAlbum());


        ExistingPlaylist myoldOne = new ExistingPlaylist();

        myoldOne.setPlaylistName("BootyGains");


      //  Song prutskid = new Song("7 Days.mp3");

       // Song prutskid2 = new Song("Gold Digger.mp3");

       // myoldOne.addSong(prutskid);

       // myoldOne.addSong(prutskid2);

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

        Scanner in = new Scanner(System.in);

       // System.out.println("Enter song name: ");

       // String nameOfSong = in.nextLine();

       // changeSong(nameOfSong);


        // TESTING

    }


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
         //   checkIfCurrentQueueEqualsSelected();


            if (showSelectedItems){ // TODO HAS TO BE TRUE. REVERT

                // TODO FIX THIS METHOD SO THAT IT KEEPS TRACK OF THE SONGS - ONLY PROBLEM NOW IS THAT PLAY QUEUE IS DOUBLE FIRST TIME

                defaultTableView.getSelectionModel().select(currentQueueNumberIndex);

              //  System.out.println("Selected playlist is called: " + allPlaylistsListView.getSelectionModel().getSelectedItem());




            }





        } catch (Exception e){


            indexOutOfBoundsHandler();

        }




    }

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

        currentQueueNumberIndex = 0;

        mp.stop();

        setCurrentSong(currentPlayQueue.get(currentQueueNumberIndex));

        isPlaying=true;

        handlePlay();

        defaultTableView.getSelectionModel().select(0);



    }


    @FXML
    private void handleShuffle(){

        // TAKES A COPY OF THE PREVIOUS ARRAY LIST AND STORES IT TEMPORARY IN CASE THE USER DISABLES SHUFFLE MODE
        temporaryPlayQueue = new ArrayList<>(currentPlayQueue);

        if (!isOnShuffle){



            // TODO FIX THIS METHOD TO USE MATH RANDOM AND THEN CHANGE CURRENTQUEUENUMBERINDEX


        }



    }


    private ArrayList<Song> randomizeArrayList(ArrayList arrayList) {


        isOnShuffle=true;


        Collections.shuffle(arrayList);


        System.out.println("IS MUSIC ON SHUFFLE MODE: " + arrayList);

        return arrayList;

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

                // System.out.println(file.getName());

               mySong = new Song(file.getName());

               // FIRST IT WILL JUST SET THE TITLE TO THE FILENAME AND PRESUME THAT THERE'S NO META DATA

                mySong.setSongTitle(file.getName().substring(0,file.getName().length()-4));

                mySong.setSongArtist("Unknown artist");

                mySong.setSongAlbum("Unknown album");

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

                // TODO MAKE IT WAIT A FEW SECONDS BEFORE REFRESHING THE LIBRARY
                showLibrarySongs();

            }

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

        mp.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov)
            {
                updatesValues();
            }
        });

        // Inorder to jump to the certain part of video
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

        });




        //System.out.println(mp.getCurrentTime().toMinutes());      // Debug
    }


    /////////////////////////////
    // VOLUME AND MUTE CONTROL //
    /////////////////////////////


    /**
     * Will adjust the volume based on the location of the grabber on the slider.
     * Because of the listener, we are able to change the volume in realtime.
     *
     */

    @FXML
    private void adjustVolume() {
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {

                // WILL MAKE SURE THAT IF THE MUSIC IS MUTED AND THE USER CHANGES THE VOLUME, THE FULL SOUND ICON WILL REAPPEAR
                String pathVolumeIcon = "/sample/buttons/volumeFull.png";

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

    Iterator<String> itr ;


    private void setPlayQueue(ArrayList playQueueArrayList){

        currentPlayQueue = playQueueArrayList;

        // WILL CHECK THE LOCATION OF THE SONG YOU ARE CURRENTLY LISTENING TO. THIS IS SO THAT IT WONT RESTART THE PLAYLIST
        currentQueueNumberIndex = -1;



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
     * TODO WRITE A DESCRIPTION
     * @param songArrayList
     */

    private void autoSkipping(ArrayList songArrayList){






    }






    private void setCurrentSong(Song currentSong) {


        // Will pass the parameter to the constructor
        // Song currentSong = new Song(fileName);

        // WILL MAKE A REFERENCE TO INSTANCE VARIABLE
        mySong = currentSong; //TODO CHECK IF THIS IS NECESSARY

        me = currentSong.getMedia();

        mp = new MediaPlayer(me);
        //
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


                        albumCoverView.setImage((Image)value);

                        // TODO FIND A WAY TO REMOVE THE LISTENER
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


    @FXML
    private void showLibrarySongs(){

        // WILL CHECK IF USER IS IN THE PLAYLIST THAT WE ARE LISTENING TO

        tempPlaylistName="Library";

        if (tempPlaylistName.equals(listeningPlaylistName)){

            showSelectedItems=true;

        } else {

            showSelectedItems=false;

        }


        System.out.println("You pressed Library");

        defaultListView.setMaxWidth(0);

        defaultListView.setDisable(true);

        // BOOLEANS CHANGED:

        libraryVisible=true;

        // TODO MAKE SURE THAT THIS IS ONLY THE NEXT TIME YOU PRESS IT, AND NOT DURING INITIALIZATION
        selectedNewPlaylist=true;


        library = new Library();

        library.displayAllSongs(defaultTableView,songTitleColumn,songArtistColumn,songAlbumColumn);

        // TESTING AREA


        library.resetArrayList();

        library.retrieveAllSongs();

        library.getSongsFoundArrayList();

        temporaryPlayQueue = library.getSongsFoundArrayList();

        System.out.println("This is what I got from library index 1: " + library.getSongsFoundArrayList().get(0).getFileName() + " - Check if correct");

        System.out.println("The size of temporaryPlayQueue is: " + temporaryPlayQueue.size());

        //


        // WILL GIVE US THE PLAY QUEUE
    //    setPlayQueue(library.getSongsFoundArrayList());


    }

    private void displayAllPlaylist(ListView listView){

        allPlaylists = new ExistingPlaylist();


        allPlaylists.showAllPlaylists(listView);

    }



















    private void setListView(String searchKeyword){




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

    // NOT USED

    private void checkMetaDataTester(Song song){

        System.out.println("Name is: " + song.getSongTitle());

        System.out.println("Artist is: " + song.getSongArtist());

        System.out.println("Album is: " + song.getSongAlbum());



    }


    // DATA BASE METHODS

    public static void emptyData(){


        do{
            String data = DB.getDisplayData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }

        } while(true);


    }

    public String getAllData(){

        String data = "";

        do {
            data += DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                System.out.print(data);
            }
        } while (true);

        return data;

    }


    private void songPlayingNow(Song song){

    }


    // VARIABLES THAT WILL CHECK IF YOU SELECTED A NEW PLAYLIST OR NOT

    private String tempPlaylistName = "Library";

    private String listeningPlaylistName = "Library";


    private void checkIfCurrentQueueEqualsSelected(){

        if (tempPlaylistName.equals(listeningPlaylistName)){

            showSelectedItems=true;

        } else {

            showSelectedItems=false;

        }


    }



    @FXML
    private void getSongSelected(){


       try{

           mySong = defaultTableView.getSelectionModel().getSelectedItem();

           // WILL PRINT IT OUT IN THE CONSOLE
           System.out.println("Playing: " + mySong.getFileName());

           // WILL STOP THE MUSIC IF THERE WAS ANY PLAYING
           handleStop();


           setCurrentSong(mySong);


           listeningPlaylistName=tempPlaylistName;

           System.out.println("1. Song comes from: " + listeningPlaylistName);

           showSelectedItems=true;


           System.out.println("2. Playlist that was selected las time: " + tempPlaylistName);

           // TESTING


           if (selectedNewPlaylist){

               System.out.println("Selected new playlist and started playing it");

              // currentPlaylistPlaying= defaultListView.getSelectionModel().getSelectedItem().getPlaylistName();

             //  System.out.println("+Current playlist is: " + currentPlaylistPlaying);

               for (int i = 0; i < temporaryPlayQueue.size(); i++) {

                   System.out.println("---Play queue to be played: " + temporaryPlayQueue.get(i).getFileName());

               }


               // WILL CONFIGURE A NEW QUEUE GIVEN THAT THE USER SELECTED A NEW PLAYLIST AND A NEW SONG AS WELL
               setPlayQueue(temporaryPlayQueue);


           }

           // WILL AUTOMATICALLY PLAY THE SONG WHEN YOU CLICK ON IT
           //  mp.setAutoPlay(true); TODO CURRENTLY CLASHING WITH THE PLAY CONTROLS


       } catch (Exception e){

           // TODO EXCEPTION ERROR

           System.out.println(e.getMessage());
           System.out.println("Nothing was selected, couldn't play a song");

       }




    }

    @FXML
    private void getPlaylistSelected(){


        selectedNewPlaylist = true;

        String selectedPlaylist = allPlaylistsListView.getSelectionModel().getSelectedItem();

        System.out.println("Name of selected playlist: " + selectedPlaylist);

        ExistingPlaylist existingPlaylist = new ExistingPlaylist();

        existingPlaylist.setPlaylistName(selectedPlaylist);

        existingPlaylist.displayAllSongs(defaultTableView,songTitleColumn,songArtistColumn,songAlbumColumn);

        System.out.println("Array size: " + existingPlaylist.getSongsFoundArrayList().size());

       // System.out.println(existingPlaylist.getSongsFoundArrayList().get(0).getFileName());

       // System.out.println(existingPlaylist.getSongsFoundArrayList().get(1).getFileName());

        if (existingPlaylist.getSongsFoundArrayList().size()>0){

            System.out.println("Made a new queue for the new playlist");

            // Will save this queue and make it ready for when you start a new playlist
            temporaryPlayQueue = new ArrayList<>(existingPlaylist.getSongsFoundArrayList());


            tempPlaylistName=selectedPlaylist;

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




    private boolean windowOpen = false;

    /**
     * Will initialize a window, where the user gets to choose a name for his new playlist.
     * @throws IOException
     */
    @FXML
    private void createNewPlaylist() throws IOException {


        PopupController playlistCreationWindow = new PopupController();

        playlistCreationWindow.createPlaylistPopup();



    }





}

