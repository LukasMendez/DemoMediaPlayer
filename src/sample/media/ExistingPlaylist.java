package sample.media;

import javafx.scene.control.ListView;
import sample.DB;
import sample.Playlist;
import sample.Song;

import java.util.ArrayList;

/**
 *
 * This class to modify existing playlist
 * Here you can search and find the specific one, that you are looking for
 *
 */


public class ExistingPlaylist extends Playlist {

    private String playlistName;


    public void searchForPlaylist(String searchName, ListView listView){


        // WILL SEARCH FOR THE GIVEN NAME IN THE PLAYLIST
        DB.selectSQL("SELECT fldName FROM tblPlaylist\n" +
                "                    WHERE CHARINDEX('"+searchName+"', fldName) > 0");


        int amountOfResults = checkAmountOfResults();

        configurePlaylistName(amountOfResults, searchName, listView);




    }



    private int checkAmountOfResults(){

        int count = 0;

        System.out.println();

        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }else{

                count+=1;
                System.out.println(data);
            }
        } while(true);


        if (count==1) {System.out.println("\nFound " + count + " result!");}
        else { System.out.println("\nFound " + count + " results!"); }


        return count;
    }



    private void configurePlaylistName(int amountOfResults, String playlistName, ListView listView){

        // IF THERE'S ONLY ONE RESULT. WE ASSUME THIS IS THE PLAYLIST THAT THE USER IS LOOKING FOR
        if (amountOfResults==1){

            DB.selectSQL("SELECT fldName FROM tblPlaylist\n" +
                    "                    WHERE CHARINDEX('"+playlistName+"', fldName) > 0");

            this.playlistName=DB.getData();

            emptyData();

        // ELSE IT WILL PROVIDE A VISUAL LIST OF THE SEARCH RESULTS
        } else {

            DB.selectSQL("SELECT fldName, fldPlaylistID FROM tblPlaylist\n" +
                    "                    WHERE CHARINDEX('"+playlistName+"', fldName) > 0");


            displayResults(listView);







        }



    }


    private void displayResults(ListView listView){

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
        for (int i = 0; i < arrayList.size(); i++) {


                listView.getItems().add(arrayList.get(i));


        }

    }


    // TODO CHECK IF THIS IS EVEN NECESSARY
    public void addSongTOBEDELETED(Song song){

        // WILL TAKE THE CURRENT SONG GET ITS FILENAME (PRIMARY KEY ON tblSong) AND THE PLAYLIST'S ID (PRIMARY KEY ON tblPlaylist)
        // AND THEN INSERT THEM INTO OUR "INTERSECTION ENTITY" CALLED tblPlaylistSongs
        DB.insertSQL("insert into tblPlaylistSongs values('"+song.getFileName()+"','"+this.playlistName+"')");


    }


    public String getPlaylistName(){


        return playlistName;
    }


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

       ExistingPlaylist existingPlaylist = new ExistingPlaylist();






     //   System.out.println("The playlist name choosen for the object is: " + existingPlaylist.getPlaylistName());


    }



}
