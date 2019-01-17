package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import playlistManagement.ExistingPlaylist;
import playlistManagement.Library;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * This class represents the popup window, where the user gets to select the songs he want to add to the
 * chosen playlist.
 *
 * @author Lukas, Pierre, Alexander and Allan
 *
 */


public class AddSongController implements Initializable {


    // FXML

    @FXML
    private Button doneButton;


    // DISPLAYING PLAYLISTS AND SONGS

    @FXML
    private TableView<Song> popupTableView;

    // 1) Song Title

    @FXML
    private TableColumn<Song,String> popupSongTitleColumn;

    // 2) Song artist

    @FXML
    private TableColumn<Song,String>  popupSongArtistColumn;

    // 3) Song album
    @FXML
    private TableColumn<Song,String> popupSongAlbumColumn;



    // MADE STATIC AS IT HAS TO BE APPLICABLE FOR ALL OBJECTS

    // You can only select one playlist at a time
    private static String currentPlaylistSelected;


    private Library defaultLibrary = new Library();



    public void initialize(URL location, ResourceBundle resources){


        currentPlaylistSelected = Controller.getTempPlaylistName();

        System.out.println("Initializer in AddSongController says - currentPlaylistSelected is: " + currentPlaylistSelected);

        showListOfAvailableSongs();


    }


    /**
     * Will show a list in the popup window of all the songs, that aren't in the playlist already.
     * This is a great way to avoid duplicates and confusion. The method calls several methods in the process from classes
     * such as Library, Playlist and Song.
     *
     */

    @FXML
    private void showListOfAvailableSongs(){


        System.out.println("CurrentPlaylist: " + currentPlaylistSelected);

        defaultLibrary.setNameOfPlaylist(currentPlaylistSelected);

        // THIS METHOD RETRIEVES THE SONGS THAT AREN'T IN THE PLAYLIST
        defaultLibrary.retrieveNonIncludedSongs();

        // IF THERE ARE AT LEAST ONE IT WILL TELL YOU THE NAME FOR DEBUGGING PURPOSES AND DISPLAY THE SONGS
        if (defaultLibrary.getSongsFoundArrayList().size()>0) {

            System.out.println("Song number 1 is: " + defaultLibrary.getSongsFoundArrayList().get(0).getFileName());


            defaultLibrary.displayLeftovers(popupTableView,popupSongTitleColumn,popupSongArtistColumn,popupSongAlbumColumn);

        }

    }

    /**
     * Will add the song that you selected in the TableView to the playlist.
     * After that, it will refresh the list. But in the users eyes, it will just look like the song he selected
     * disappeared naturally.
     *
     */
    @FXML
    private void addCurrentSong(){

        Song song = popupTableView.getSelectionModel().getSelectedItem();

        System.out.println("addCurrentSong says that song name is: " + song.getFileName());

        System.out.println("addCurrentSong says that playlist name is: " + currentPlaylistSelected);

        DB.insertSQL("insert into tblPlaylistSongs values('"+song.getFileName()+"', '"+currentPlaylistSelected+"')");

        System.out.println("Added " + song.getFileName() + " to the playlist: " + currentPlaylistSelected);

        // WILL REMOVE EVERYTHING FROM THE OLD ARRAY LIST AND UPDATE IT WITH A NEW ONE

        defaultLibrary.resetArrayList();

        // THIS IS WHERE THE LIST IS REFRESHED
        showListOfAvailableSongs();


    }


    /**
     * This method closes the window, and is attached to the "doneButton"
     */

    @FXML
    private void finishedAddingSongs(){

        // TEMPORARY SOLUTION FOR CLOSING THE WINDOW
        ((Stage)doneButton.getScene().getWindow()).close();

    }


}



