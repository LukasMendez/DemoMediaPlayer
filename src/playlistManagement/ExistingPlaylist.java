package playlistManagement;

import javafx.scene.control.ListView;
import sample.DB;
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





    public ExistingPlaylist(){

        // INVOKE THE DEFAULT CONSTRUCTOR IN THE SUPERCLASS
        super();

    }

    /**
     * Only used when you know the playlistName on purpose. If you're unsure, then you use the search method.
     * @param playlistName
     */
    public void setPlaylistName(String playlistName){

        this.playlistName=playlistName;

        super.setNameOfPlaylist(this.playlistName);




    }


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

        // WILL PROVIDE A VISUAL LIST OF THE SEARCH RESULTS
        if (amountOfResults>0){

            DB.selectSQL("SELECT fldName FROM tblPlaylist\n" +
                    "                    WHERE CHARINDEX('"+playlistName+"', fldName) > 0");


            displayData(listView);

        } else {

            System.out.println("Nothing to display. Search machine found nothing.");

        }

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
        for (int i = 0; i < arrayList.size(); i++) {


                listView.getItems().add(arrayList.get(i));


        }

    }





    // TODO MAKE IT POSSIBLE FOR THE USER TO SELECT A PLAYLIST. THAT PLAYLIST WILL BE SET TO THE INSTANCE VARIABLE

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
