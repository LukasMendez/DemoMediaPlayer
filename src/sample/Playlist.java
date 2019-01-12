package sample;

/**
 *
 * This class is only for creating new playlists and modifying them afterwards.
 * If you want to access an existing playlist, please use "Existing playlist"
 *
 */


public class Playlist {

    private String nameOfPlaylist;


    // DEFAULT CONSTRUCTOR
    public Playlist(){


    }

    public Playlist(String nameOfPlaylist){

        this.nameOfPlaylist = nameOfPlaylist;

        // WILL CREATE AND EMPTY PLAYLIST WITH THE NAME YOU GAVE IT
        DB.insertSQL("insert into tblPlaylist values('"+nameOfPlaylist+"')");


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

