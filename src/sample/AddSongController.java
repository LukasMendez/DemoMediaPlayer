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

public class AddSongController implements Initializable {


    // FXML

    @FXML
    private Button doneButton;

    @FXML
    private Button addSongButton;



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
    private static boolean windowOpen = false;

    // You can only select one playlist at a time
    private static String currentPlaylistSelected;





    private Parent sourceOfWindow;

    private Stage addingWindow;

    private Scene windowScene;

    private Library defaultLibrary = new Library();



    public AddSongController(){




    }



    public void initialize(URL location, ResourceBundle resources){


        currentPlaylistSelected = Controller.getTempPlaylistName();

        System.out.println("Initializer in AddSongController says - currentPlaylistSelected is: " + currentPlaylistSelected);

        showListOfAvailableSongs();


    }



    @FXML
    private void showListOfAvailableSongs(){


        System.out.println("CurrentPlaylist: " + currentPlaylistSelected);

        defaultLibrary.setNameOfPlaylist(currentPlaylistSelected);

        defaultLibrary.retrieveNonIncludedSongs();

        if (defaultLibrary.getSongsFoundArrayList().size()>0) {

            System.out.println("Song number 1 is: " + defaultLibrary.getSongsFoundArrayList().get(0).getFileName());


            // TODO FIX SO THAT IT WILL SHOW UP IN THE TABLE VIEW

            defaultLibrary.displayLeftovers(popupTableView,popupSongTitleColumn,popupSongArtistColumn,popupSongAlbumColumn);


        }



    }

    @FXML
    private void addCurrentSong(){

        Song song = popupTableView.getSelectionModel().getSelectedItem();

        System.out.println("addCurrentSong says that song name is: " + song.getFileName());

        System.out.println("addCurrentSong says that playlist name is: " + currentPlaylistSelected);

        DB.insertSQL("insert into tblPlaylistSongs values('"+song.getFileName()+"', '"+currentPlaylistSelected+"')");

        System.out.println("Added " + song.getFileName() + " to the playlist: " + currentPlaylistSelected);

        // WILL REMOVE EVERYTHING FROM THE OLD ARRAY LIST AND UPDATE IT WITH A NEW ONE

        defaultLibrary.resetArrayList();

        showListOfAvailableSongs();


    }




    @FXML
    private void finishedAddingSongs(){

        windowOpen=false;

        // TEMPORARY SOLUTION FOR CLOSING THE WINDOW
        ((Stage)doneButton.getScene().getWindow()).close();



    }


    public static void main(String[] args) {

//        showListOfAvailableSongs();



    }




}



