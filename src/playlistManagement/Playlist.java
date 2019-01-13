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
 * This class is only for creating new playlists and modifying them afterwards.
 * If you want to access an existing playlist, please use "Existing playlist"
 *
 */


public class Playlist {



    protected String nameOfPlaylist;

    protected int amountOfSongs;

    protected ArrayList<Song> songsFoundArrayList = new ArrayList<>();

    // Will give you the possibility to get the complete TableView
    private TableView <Song> songTableView;


    // DEFAULT CONSTRUCTOR
    public Playlist(){


    }

    public Playlist(String nameOfPlaylist){

        this.nameOfPlaylist = nameOfPlaylist;

        // WILL CREATE AND EMPTY PLAYLIST WITH THE NAME YOU GAVE IT
        DB.insertSQL("insert into tblPlaylist values('"+nameOfPlaylist+"')");


    }

    // TODO MAY WANNA DELETE THIS

    /**
     * Only used by other classes to set the name of the existing playlist.
     * @param nameOfPlaylist
     */
    protected void setNameOfPlaylist(String nameOfPlaylist) {
        this.nameOfPlaylist = nameOfPlaylist;
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

                do{
                    String data = DB.getData();
                    if (data.equals(DB.NOMOREDATA)){
                        System.out.println("NO MORE DATA");
                        break;
                    } else {

                        System.out.println("Added this song to the Song ArrayList: " + data);

                        // WILL GIVE US THE FILENAME AND PASS IT TO THE PARAMETERS IN THE SONG OBJECT
                        Song song = new Song(data);



                        // TODO MAKE IT DOWNLOAD THE PROPERTIES FROM THE DATA BASE AFTER THIS

                        // WILL ADD THE SONG INCLUDING ITS PROPERTIES TO THE ARRAY LIST
                        songsFoundArrayList.add(song);

                    }

                } while(true);

                // WILL APPLY META DATA TO ALL THE SONGS IN THE ARRAY LIST
                applyMetaDataToSongs();

             //   System.out.println("Just testing to see if the metadata is actually here. For song 1 heres what we got: " + songsFoundArrayList.get(0).getSongTitle() + " and " + songsFoundArrayList.get(0).getSongArtist());


                // WILL CONFIGURE THE ACTUAL DISPLAY DATA
                setSongTableView(tableView,titleColumn,artistColumn,albumColumn);

                // TODO MAKE IT SHOW UP ON EITHER THE LIST VIEW OR TABLE VIEW




            } else {

                System.out.println("There's no playlist with that name. Please check for typos!");
            }
        } else {

            System.out.println("Not able to display anything as no playlist were selected");

        }
    }


    protected void applyMetaDataToSongs(){


        // WILL INITIALIZE THE VALUES FROM THE DATABASE

        for (int i = 0; i < songsFoundArrayList.size(); i++) {

            songsFoundArrayList.get(i).getSongTitleFromDB();

            songsFoundArrayList.get(i).getSongArtistFromDB();

            songsFoundArrayList.get(i).getSongAlbumFromDB();



        }





    }



    @FXML
    public void setSongTableView(TableView tableView, TableColumn titleColumn, TableColumn artistColumn, TableColumn albumColumn){

        titleColumn.setCellValueFactory(new PropertyValueFactory<Song,String>("songTitle"));

        artistColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("songArtist"));

        albumColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("songAlbum"));






        // WILL CALL A METHOD THAT GETS THE SONGS AND ITS PROPERTIES
        tableView.setItems(getSongs());

      //  tableView.getColumns().addAll(titleColumn,artistColumn,albumColumn);




    }


    /**
     * This methods will return an ObservableList of Songs objects
     * @return song objects
     */

    protected ObservableList<Song> getSongs(){

        ObservableList<Song> songs = FXCollections.observableArrayList();

        for (int i = 0; i < amountOfSongs; i++) {

            // WILL RETRIEVE EACH SONG FROM THE ARRAY LIST AND SAVE IT INTO THE OBSERVABLE LIST
            songs.add(songsFoundArrayList.get(i));

        }

        return songs;


    }







    /**
     * Used for storing SQL data in an ArrayList here in Java
     * @param arrayList the ArrayList where you want to save it
     */


    // TODO MAY WANNA DELETE THIS NOW THAT WE ARE USING AN OBSERVABLE LIST
    private void addSQLDataToArrayList(ArrayList arrayList){

        int count = 0;

        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){


                if (count==0){
                    // If there's no SQL data to be saved this will show up in the console
                    System.out.println("There was no SQL data to be saved! Check if you have loaded the methods in the right order");
                }

                break;
            }else{
                // WE ADD EACH ELEMENT TO THE ARRAY LIST
                arrayList.add(data);
                count+=1;

            }
        } while(true);


    }




    /**
     * Just to clear all data from the buffer
     */

    private static void emptyData(){


        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }

        } while(true);


    }



    ///////////////////
    // TESTING AREA  //
    ///////////////////

    public static void main(String[] args) {


        Playlist playlist = new Playlist("Best Mumble Rappers");

      //  emptyData();

        Song song = new Song("a lot.mp3");

        playlist.addSong(song);





    }




}

