package playlistManagement;

import javafx.scene.control.ListView;
import sample.DB;
import sample.Song;

import java.util.ArrayList;

/**
 *
 * This class is to modify existing playlist, not to create a new one.
 * It is though a subclass to the superclass, which is "Playlist".
 *
 * The main reason why they are separated is to make it easier to distinguish between CREATION and ACCESS.
 * Playlist is mostly for creating a new playlist AND THEN modifying it, where ExistingPlaylist is to accessing
 * one you created earlier.
 *
 * @author Lukas, Pierre, Alexander and Allan
 *
 */


public class ExistingPlaylist extends Playlist {


    private String playlistName;


    /**
     * Default constructor that also invokes the super constructor
     */

    public ExistingPlaylist(){

        super();

    }

    /**
     * This is where you select the playlist name
     * @param playlistName the name of your playlist
     */
    public void setPlaylistName(String playlistName){

        this.playlistName=playlistName;

        super.setNameOfPlaylist(this.playlistName);

    }



    public void showAllPlaylists(ListView listView){


        DB.selectSQL("select fldName from tblPlaylist");

        displayData(listView);

    }


    /**
     * This method will display the selected data in the List View
     * @param listView the list view that you are trying to display the data on.
     */


    public void displayData(ListView listView){

        // WE CREATE AN ARRAY LIST TO STORE THE RESULTS
        ArrayList<String> arrayList = new ArrayList<>();

        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{
                // WE ADD EACH ELEMENT TO THE ARRAY LIST
                arrayList.add(data);
                System.out.print(data);
            }
        } while(true);



        // THE LIST VIEW WILL DISPLAY EACH ELEMENT ONE BY ONE
        // THE LOOP WILL ONLY RUN AS MANY TIMES AS THERE ARE ITEMS (SEARCH RESULTS)

        listView.getItems().clear();

        for (int i = 0; i < arrayList.size(); i++) {



                listView.getItems().add(arrayList.get(i));


        }

    }

    /**
     * Get the playlist name
     * @return playlistname
     */

    public String getPlaylistName(){


        return playlistName;
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
