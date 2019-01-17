package playlistManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.DB;
import sample.Song;

import java.util.ArrayList;

/**
 *
 * This class is mainly for creating new playlists and modifying them afterwards.
 * If you want to access an existing playlist, please use "Existing playlist".
 *
 * @author Lukas, Pierre, Alexander and Allan
 *
 */


public class Playlist {


    // THESE VARIABLES ARE PROTECTED BECAUSE OTHER CLASSES IN THE PACKAGE ARE INTERESTED IN THEM

    protected String nameOfPlaylist;

    protected int amountOfSongs;

    protected ArrayList<Song> songsFoundArrayList = new ArrayList<>();


    /**
     * Default constructor
     */

    // DEFAULT CONSTRUCTOR
    public Playlist(){

    }


    /**
     * Constructor where you pass the name of the playlist, that you wanna create. The data will then be inserted
     * into the database.
     * @param nameOfPlaylist the name of the playlist
     */

    // CONSTRUCTOR
    public Playlist(String nameOfPlaylist){

        this.nameOfPlaylist = nameOfPlaylist;

        // WILL CREATE AND EMPTY PLAYLIST WITH THE NAME YOU GAVE IT
        DB.insertSQL("insert into tblPlaylist values('"+nameOfPlaylist+"')");


    }


    /**
     * Only used by other classes to set the name of the existing playlist. Can sometimes be necessary if the subclass
     * is working with methods from the superclass that uses this specific variable.
     * @param nameOfPlaylist name of the playlist
     */
    public void setNameOfPlaylist(String nameOfPlaylist) {
        this.nameOfPlaylist = nameOfPlaylist;
    }


    /**
     * Will retrieve all the songs EXCEPT the ones from the current playlist selected.
     * This is an extension to the add songs function, where the user can add songs to his playlist, that
     * are not already in the playlist. Uses SQL-principles.
     *
     */

    public void retrieveNonIncludedSongs(){

        if (nameOfPlaylist!=null){

            countSongsInPlaylist();

            DB.selectSQL("select fldName from tblSong except select fldSongName from tblPlaylistSongs where fldPlaylistName='"+nameOfPlaylist+"'");

            loadDataOfLibrary();


        } else {

            System.out.println("Cannot retrieve songs, as no playlist were selected");

        }



    }

    /**
     * This method will load the selected data from SQL and apply the meta data
     */

    protected void loadDataOfLibrary(){


        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){

                break;
            } else {


                // DEBUGGING
                // System.out.println("Added this song to the Song-library ArrayList: " + data);


                // WILL GIVE US THE FILENAME AND PASS IT TO THE PARAMETERS IN THE SONG OBJECT
                Song song = new Song(data);



                // WILL ADD THE SONG INCLUDING ITS PROPERTIES TO THE ARRAY LIST
                songsFoundArrayList.add(song);


            }

        } while(true);

        // WILL APPLY META DATA TO ALL THE SONGS IN THE ARRAY LIST
        applyMetaDataToSongs();


    }



    /**
     * Will make you add a song to the new playlist that you created or an existing playlist.
     * If you want to do it from an existing playlist you have to invoke the ExistingPlaylist constructor first.
     * @param song is the song you are adding to the playlist.
     */
    public void addSong(Song song){


        // Will only allow you to add a song if the playlist name has been initialized
        if (nameOfPlaylist!=null){

            // WILL TAKE THE CURRENT SONG GET ITS FILENAME (PRIMARY KEY ON tblSong) AND THE PLAYLIST'S ID (PRIMARY KEY ON tblPlaylist)
            // AND THEN INSERT THEM INTO OUR "INTERSECTION ENTITY" CALLED tblPlaylistSongs
            DB.insertSQL("insert into tblPlaylistSongs values('"+song.getFileName()+"','"+this.nameOfPlaylist+"')");

        } else {

            System.out.println("Could not add song, as the user hasn't created or selected a playlist yet");

        }



    }


    /**
     * Will count all the songs in the playlist that you've chosen
     */

    private void countSongsInPlaylist(){

        // WILL COUNT THE AMOUNT OF SONGS IN THE PLAYLIST
        if (nameOfPlaylist!=null){

            DB.selectSQL("SELECT COUNT(fldSongName) from tblPlaylistSongs where fldPlaylistName='"+nameOfPlaylist+"'");

            amountOfSongs = Integer.parseInt(DB.getData());

            System.out.println("There are " + amountOfSongs + " songs in the playlist named: " + nameOfPlaylist);

            // WILL CLEAR THE BUFFER JUST FOR SAFETY
            emptyData();

        }

    }

    /**
     * Will display all the selected data. The difference between this, and all the other "Display*" methods is that
     * this one doesn't actually select anything for you. It just retrieves whatever it finds based on the newest SQL
     * selection that you've made.
     */

    protected void displaySelectedData(){


        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                System.out.println("NO MORE DATA");
                break;
            } else {

                System.out.println("Added this song to the Song ArrayList: " + data);

                // WILL GIVE US THE FILENAME AND PASS IT TO THE PARAMETERS IN THE SONG OBJECT
                Song song = new Song(data);


                // WILL ADD THE SONG INCLUDING ITS PROPERTIES TO THE ARRAY LIST
                songsFoundArrayList.add(song);

            }

        } while(true);

        // WILL APPLY META DATA TO ALL THE SONGS IN THE ARRAY LIST
        applyMetaDataToSongs();



    }


    /**
     * Will display all of the songs from the given playlist name
     * @param tableView the TableView you want to display the data on
     * @param titleColumn the column for the song title
     * @param artistColumn the column for the song artist
     * @param albumColumn the column for the song album
     */

    @FXML
    public void displayAllSongs(TableView tableView, TableColumn titleColumn, TableColumn artistColumn, TableColumn albumColumn){

        // Will not run unless there's a playlist name
        if (nameOfPlaylist!=null){

            // WILL COUNT THE AMOUNT OF SONGS IN THE PLAYLIST AND INITIALIZE "amountOfSongs"
            // WILL CLEAR THE BUFFER FOR NEXT SQL STATEMENT
            countSongsInPlaylist();



            // WILL GET THE NAME OF THE SONGS IN THE PLAYLIST
            DB.selectSQL("select fldSongName from tblPlaylistSongs where fldPlaylistName='"+nameOfPlaylist+"'");


            if (amountOfSongs>0){

                displaySelectedData();


            } else {

                System.out.println("There's nothing to display");
                songsFoundArrayList.clear();


            }


            // WILL CONFIGURE THE ACTUAL DISPLAY DATA
            setSongTableView(tableView,titleColumn,artistColumn,albumColumn);


        } else {

            System.out.println("Not able to display anything as no playlist were selected");

        }
    }

    /**
     * Will get the ArrayList of songs, that you've made using the other methods.
     * Often used for updating the song queue.
     * @return ArrayList of songs.
     */

    public ArrayList<Song> getSongsFoundArrayList(){


        return songsFoundArrayList;


    }


    /**
     * This method will assign and get information about the metadata for each song.
     *
     */

    protected void applyMetaDataToSongs(){


        // WILL INITIALIZE THE VALUES FROM THE DATABASE

        for (int i = 0; i < songsFoundArrayList.size(); i++) {

            songsFoundArrayList.get(i).getSongTitleFromDB();

            songsFoundArrayList.get(i).getSongArtistFromDB();

            songsFoundArrayList.get(i).getSongAlbumFromDB();

        }

    }


    /**
     * This method will insert the data into the Table View.
     * @param tableView the TableView you want to display the data on
     * @param titleColumn the column for the song title
     * @param artistColumn the column for the song artist
     * @param albumColumn the column for the song album
     */

    @FXML
    public void setSongTableView(TableView tableView, TableColumn titleColumn, TableColumn artistColumn, TableColumn albumColumn){

        titleColumn.setCellValueFactory(new PropertyValueFactory<Song,String>("songTitle"));

        artistColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("songArtist"));

        albumColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("songAlbum"));


        // WILL CALL A METHOD THAT GETS THE SONGS AND ITS PROPERTIES
        tableView.setItems(getSongs());

    }


    /**
     * This methods will return an ObservableList of Songs objects. This kind of object is better supported in terms
     * of displaying data in the TableView. Therefore it is used instead of the ArrayList itself.
     * @return song objects
     */

    protected ObservableList<Song> getSongs(){

        ObservableList<Song> songs = FXCollections.observableArrayList();

        System.out.println("getSongs() says that songFoundArrayList is: " + songsFoundArrayList.size());


        for (int i = 0; i < songsFoundArrayList.size(); i++) {

            // WILL RETRIEVE EACH SONG FROM THE ARRAY LIST AND SAVE IT INTO THE OBSERVABLE LIST
            songs.add(songsFoundArrayList.get(i));

        }

        return songs;


    }


    /**
     * This method was made based on the existing functions in the DB Class. The purpose is to empty the buffer, so that you
     * dont have any pending data.
     */

    private static void emptyData(){


        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }

        } while(true);


    }


}

