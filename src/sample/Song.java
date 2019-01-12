package sample;



import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import javafx.scene.media.*;

import java.io.File;

public class Song {

    private boolean doneSearching = false;


    private String fileName;

    private String songTitle;
    private String songArtist;
    private String songAlbum;
    private int songID;

    private Media media;



    public Song (String fileName){

        this.fileName=fileName;

        // The path to the selected song
        String path = new File("src/sample/media/" + fileName).getAbsolutePath();

        // Adding the path to the media
        media = new Media(new File(path).toURI().toString());


    }

    // SETTERS

    public void setSongTitle(String songTitle) {

        this.songTitle = songTitle;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }


    // GETTERS

    public String getSongTitle() {


        return songTitle;

    }

    public String getSongArtist() {


        return songArtist;
    }


    public String getSongAlbum() {


        return songAlbum;
    }

    public Media getMedia() {
        return media;
    }

    public String getFileName(){

        return fileName;
    }


    // GETTERS FROM THE DATABASE

    public String getSongTitleFromDB() {

        DB.selectSQL("select fldTitle from tblSong where fldName='" + this.fileName+"'");

        songTitle = DB.getData();

        // EMPTY PENDING DATA
        emptyData();

        return songTitle;

    }


    public String getSongArtistFromDB() {

        DB.selectSQL("select fldArtist from tblSong where fldName='" + this.fileName+"'");

        songArtist = DB.getData();

        // EMPTY PENDING DATA
        emptyData();

        return songArtist;
    }


    public String getSongAlbumFromDB() {

        DB.selectSQL("select fldAlbum from tblSong where fldName='" + this.fileName+"'");

        songAlbum = DB.getData();

        emptyData();

        return songAlbum;
    }





    public void applyProperties() {


        System.out.println("Applying properties..");


        ObservableMap<String,Object> meta_data=media.getMetadata();

        meta_data.addListener(new MapChangeListener<String,Object>(){
            @Override
            public void onChanged(Change<? extends String, ? extends Object> ch) {

                if(ch.wasAdded()){

                    String key=ch.getKey();
                    Object value=ch.getValueAdded();

                    switch(key){
                        case "album":
                            songAlbum = value.toString();
                          //  System.out.println(value.toString());
                            break;
                        case "artist":
                            songArtist = value.toString();
                          //  System.out.println(value.toString());
                            break;
                        case "title":
                            songTitle = value.toString();
                          //  System.out.println(value.toString());
                            doneSearching=true;
                            break;


                    }
                }
            }



        });




    } // TODO MAY WANNA DELETE THIS

    public boolean getDoneSearching(){

        return doneSearching;
    } // TODO MAY WANNA DELETE THIS


    // DATABASE RELATED METHODS

    private static void emptyData(){


        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }

        } while(true);


    }








}