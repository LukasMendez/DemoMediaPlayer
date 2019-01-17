package sample;



import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import javafx.scene.media.*;

import java.io.File;


/**
 *
 * This class represents the song itself. The song isn't just a media file. It contains a lot of information, that
 * the user and the programmer could be interested in. This is information such as file name, song title, artist, album,
 * the source and of course the media itself. This class gets you all of that.
 *
 * @author Lukas, Pierre, Alexander and Allan
 *
 */

public class Song {


    private String fileName;

    private String source;

    private String songTitle;
    private String songArtist;
    private String songAlbum;

    private Media media;
    private MediaPlayer mediaPlayer;


    /**
     * Creates a song object. Make sure that it's saved in your media folder first.
     * @param fileName the specific filename including format
     */

    public Song (String fileName){

        this.fileName=fileName;

        // The path to the selected song
        String path = new File("src/sample/media/" + fileName).getAbsolutePath();

        source = "src/sample/media/"+fileName;

        // Adding the path to the media
        media = new Media(new File(path).toURI().toString());

        mediaPlayer= new MediaPlayer(media);


    }

    /**
     * Will let you set the song title
     * @param songTitle the song title name
     */

    public void setSongTitle(String songTitle) {

        this.songTitle = songTitle;
    }

    /**
     * Will let you set the artist name
     * @param songArtist the song artist name
     */

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    /**
     * Will let you set the album name
     * @param songAlbum the song album name
     */

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }


    // GETTERS

    /**
     * Will get the title directly from the instance variable
     * @return title
     */

    public String getSongTitle() {

            return songTitle;

    }

    /**
     * Will get the artist directly from the instance variable
     * @return artist
     */

    public String getSongArtist() {


            return songArtist;



    }

    /**
     * Will get the album directly from the instance variable
     * @return album
     */

    public String getSongAlbum() {

            return songAlbum;



    }

    /**
     * This method will get you the media object
     * @return media
     */

    public Media getMedia() {
        return media;
    }


    /**
     * Will get you the filename of the current song instance
     * @return filename.(mp3 or wav)
     */

    public String getFileName(){

        return fileName;
    }



    // GETTERS FROM THE DATABASE. WE USE THESE WHEN WE KNOW THAT THE INFORMATION IS ALREADY IN THE DATABASE.
    // IF WE USE THE OTHER GETTERS IT CAN CONFLICT WITH THE LISTENERS.
    // THE GETTERS USUALLY FETCH THE DATA FASTER THAN THE LISTENERS ARE ABLE TO STORE THE VALUES.
    // THIS WILL JUST GIVE US THE RESULT "NULL" WHICH IS UNWANTED.


    /**
     * Will provide you the name of the title, but fetch it from the database instead
     * @return title from DB
     */


    public String getSongTitleFromDB() {

        DB.selectSQL("select fldTitle from tblSong where fldName='" + this.fileName+"'");

        songTitle = DB.getData();

        // EMPTY PENDING DATA
        emptyData();

        return songTitle;

    }


    /**
     * Will provide you the name of the artist, but fetch it from the database instead
     * @return artist from DB
     */

    public String getSongArtistFromDB() {

        DB.selectSQL("select fldArtist from tblSong where fldName='" + this.fileName+"'");

        songArtist = DB.getData();

        // EMPTY PENDING DATA
        emptyData();

        return songArtist;
    }

    /**
     * Will provide you the name of the album, but fetch it from the database instead
     * @return album from DB
     */

    public String getSongAlbumFromDB() {

        DB.selectSQL("select fldAlbum from tblSong where fldName='" + this.fileName+"'");

        songAlbum = DB.getData();

        emptyData();

        return songAlbum;
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
