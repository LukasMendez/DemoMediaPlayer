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

public class Library extends Playlist {


   // ArrayList<Song> songsFoundArrayList = new ArrayList<>();


    public Library(){

        super();

    }


    private void countSongsInLibrary(){

        // WILL COUNT THE AMOUNT OF SONGS IN THE PLAYLIST


            DB.selectSQL("SELECT COUNT(fldTitle) from tblSong ");

            amountOfSongs = Integer.parseInt(DB.getData());

            System.out.println("There are " + amountOfSongs + " songs in the library");

            // WILL CLEAR THE BUFFER JUST FOR SAFETY
            emptyData();





    }

    public void displayAllSongs(TableView tableView, TableColumn titleColumn, TableColumn artistColumn, TableColumn albumColumn){



            // WILL COUNT THE AMOUNT OF SONGS IN THE PLAYLIST AND INITIALIZE "amountOfSongs"
            // WILL CLEAR THE BUFFER FOR NEXT SQL STATEMENT
            countSongsInLibrary();



            // WILL GET THE NAME OF THE SONGS IN THE PLAYLIST
            DB.selectSQL("select fldName from tblSong");


            if (amountOfSongs>0){

                do{
                    String data = DB.getData();
                    if (data.equals(DB.NOMOREDATA)){

                        break;
                    } else {

                     // DEBUGGING   System.out.println("Added this song to the Song-library ArrayList: " + data);

                        // WILL GIVE US THE FILENAME AND PASS IT TO THE PARAMETERS IN THE SONG OBJECT
                        Song song = new Song(data);



                        // WILL ADD THE SONG INCLUDING ITS PROPERTIES TO THE ARRAY LIST
                        songsFoundArrayList.add(song);

                    }

                } while(true);

                // WILL APPLY META DATA TO ALL THE SONGS IN THE ARRAY LIST
                applyMetaDataToSongs();

                //   System.out.println("Just testing to see if the metadata is actually here. For song 1 heres what we got: " + songsFoundArrayList.get(0).getSongTitle() + " and " + songsFoundArrayList.get(0).getSongArtist());


                // WILL CONFIGURE THE ACTUAL DISPLAY DATA
                setSongTableView(tableView,titleColumn,artistColumn,albumColumn);





            } else {

                System.out.println("There's no songs in the library");
            }

    }






    /**
     * This methods will return an ObservableList of Songs objects
     * @return song objects
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
