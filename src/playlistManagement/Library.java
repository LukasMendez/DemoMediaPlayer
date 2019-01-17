package playlistManagement;

import javafx.scene.control.*;
import sample.DB;


/**
 *
 * This class represents the library of all the songs. Despite of that, it is still a subclass to
 * the playlist class. That's mainly due to the fact, that it uses a lot of the methods from that class.
 * For instance the possibility to display the selected data into ListView and TableView.
 * The approach to getting the songs that the user selected is built on an Array List. This Array List itself is
 * declared inside the Playlist class, therefore it makes sense to create a relation between the classes.
 *
 * @author Lukas, Pierre, Alexander and Allan
 *
 */


public class Library extends Playlist {


    /**
     * Default constructor that also invokes the superclass constructor
     */

    public Library(){

        super();

    }


    /**
     * Since the ArrayList only appends/adds it is sometimes necessary to clear it, to avoid duplicates.
     */

    public void resetArrayList(){

        songsFoundArrayList.clear();


    }

    /**
     * Will count the amount of songs in the music library
     */

    private void countSongsInLibrary(){


            DB.selectSQL("SELECT COUNT(fldTitle) from tblSong ");

            amountOfSongs = Integer.parseInt(DB.getData());

            System.out.println("There are " + amountOfSongs + " songs in the library");

            // WILL CLEAR THE BUFFER JUST FOR SAFETY
            emptyData();


    }


    /**
     * Will retrieve all the songs and add them to the ArrayList declared in the superclass
     */


    public void retrieveAllSongs(){

        // WILL COUNT THE AMOUNT OF SONGS IN THE PLAYLIST AND INITIALIZE "amountOfSongs"
        // WILL CLEAR THE BUFFER FOR NEXT SQL STATEMENT
        countSongsInLibrary();



        // WILL GET THE NAME OF THE SONGS IN THE PLAYLIST
        DB.selectSQL("select fldName from tblSong");


        if (amountOfSongs>0){


            // WILL ONLY RETRIEVE THE SELECTED DATA WHICH IN THIS CASE IS EVERYTHING
            // UNDER NORMAL CIRCUMSTANCES THE CONTENT CAN VARY A LOT
            loadDataOfLibrary();

        } else {

            System.out.println("There's no songs in the library");
        }

    }


    /**
     * The purpose of this is to show the songs from the library that aren't in the selected playlist.
     * This is so that the user can't add the same song to a playlist more than once.
     * @param tableView the TableView you want to display the data on
     * @param titleColumn the column for the song title
     * @param artistColumn the column for the song artist
     * @param albumColumn the column for the song album
     */

    public void displayLeftovers(TableView tableView, TableColumn titleColumn, TableColumn artistColumn, TableColumn albumColumn){


        setSongTableView(tableView,titleColumn,artistColumn,albumColumn);


    }

    /**
     * This method automatically retrieves all the songs and displays it on the TableView.
     * @param tableView the TableView you want to display the data on
     * @param titleColumn the column for the song title
     * @param artistColumn the column for the song artist
     * @param albumColumn the column for the song album
     */

    public void displayAllSongs(TableView tableView, TableColumn titleColumn, TableColumn artistColumn, TableColumn albumColumn){




        retrieveAllSongs();




                //   System.out.println("Just testing to see if the metadata is actually here. For song 1 heres what we got: " + songsFoundArrayList.get(0).getSongTitle() + " and " + songsFoundArrayList.get(0).getSongArtist());


                // WILL CONFIGURE THE ACTUAL DISPLAY DATA
                setSongTableView(tableView,titleColumn,artistColumn,albumColumn);







    }


    /**
     * This method takes whatever the user searches for and finds as many matches in the song library as possible.
     * This method searches by chars, meaning that you don't necessarily need to know the full name, and you can search
     * for both title, artist and album, all at the same.
     * @param textField the TextField from where you can access the user input
     * @param tableView the TableView you want to display the data on
     * @param tableColumn1 the column for the song title
     * @param tableColumn2 the column for the song artist
     * @param tableColumn3 the column for the song album
     */

    public void searchForSongs(TextField textField, TableView tableView, TableColumn tableColumn1, TableColumn tableColumn2, TableColumn tableColumn3){

        // WILL GET THE WORD THAT YOU SEARCHED FOR
        String searchWord = textField.getText();

        DB.selectSQL("SELECT COUNT(fldName) from tblSong WHERE (CHARINDEX('"+searchWord+"', fldTitle) > 0 or CHARINDEX('"+searchWord+"', fldArtist) > 0 or CHARINDEX('"+searchWord+"', fldAlbum) > 0 )");

        int searchResultsFound = 0;

        // WILL COUNT THE AMOUNT OF SONGS IN THE PLAYLIST

            searchResultsFound = Integer.parseInt(DB.getData());

            System.out.println("There was " + searchResultsFound + " songs found");

            // WILL CLEAR THE BUFFER JUST FOR SAFETY
            emptyData();



            if (searchResultsFound>0){

                // WILL SEARCH FOR THE GIVEN NAME IN THE PLAYLIST
                DB.selectSQL("SELECT fldName FROM tblSong WHERE (CHARINDEX('"+searchWord+"', fldTitle) > 0 or CHARINDEX('"+searchWord+"', fldArtist) > 0 or CHARINDEX('"+searchWord+"', fldAlbum) > 0 )");


                displaySelectedData();


            } else {

                System.out.println("There's nothing to display. Please check for typos!");
                songsFoundArrayList.clear();


            }


        // WILL CONFIGURE THE ACTUAL DISPLAY DATA
        setSongTableView(tableView,tableColumn1,tableColumn2,tableColumn3);

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
