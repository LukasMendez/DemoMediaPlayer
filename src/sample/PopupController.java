package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import playlistManagement.Playlist;

import java.io.IOException;

/**
 *
 * This class represents the popup window, where the user can create a new playlist, and give it a name.
 *
 * @author Lukas, Pierre, Alexander and Allan
 *
 */

public class PopupController {

    @FXML
    private TextField playListNameTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button createPlayListButton;


    // MADE STATIC AS IT HAS TO BE APPLICABLE FOR ALL OBJECTS
    private static boolean windowOpen = false;

    private Stage inputWindow;

    private Parent sourceOfWindow;

    private Scene windowScene;


    /**
     * Will invoke the Constructor and crate a new Stage for the window
     * @throws IOException
     */

    public PopupController() throws IOException{

        inputWindow = new Stage();



    }

    /**
     * This method will actually generate and display the window to the user, unless it's already open.
     * @throws IOException
     */

    public void createPlaylistPopup() throws IOException {


      //  inputWindow = new Stage();
        sourceOfWindow = FXMLLoader.load(getClass().getResource("createPlaylistWindow.fxml"));
        inputWindow.getIcons().add(new Image("sample/images/Music-icon.png"));
        windowScene = new Scene(sourceOfWindow, 300, 200);
        inputWindow.setScene(windowScene);
        inputWindow.setAlwaysOnTop(true);
        inputWindow.setResizable(false);

        if (!windowOpen){

            inputWindow.show();

            windowOpen = true;

        } else {

            System.out.println("Please close the current window before you open a new one!");

        }

        // WILL CHECK IF THE USER CLOSES THE WINDOW AND MAKE YOU ABLE TO REOPEN IT AFTERWARDS
        inputWindow.setOnHiding(new EventHandler<WindowEvent>() {

            /**
             * This handy method will listen to WindowsEvents. Basically it will tell the program, that the window was closed,
             * and then set windowOpen to false, so that you can open it again.
             * @param event closing the window.
             */

            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("Playlist Creation Window closed by clicking on Close Button(X)");
                        windowOpen=false;

                    }
                });
            }
        });



    }

    /**
     * This is the submit button. This will create and save the playlist name in the database.
     * This requires, that the name does not already exist though.
     */

    @FXML
    private void submitNewPlaylist(){

        String userInput = playListNameTextField.getText();

        if (userInput!=""){

            DB.selectSQL("select count(fldName) from tblPlaylist where fldName='"+userInput+"'");

            int count = Integer.parseInt(DB.getData());

            emptyData();

            if (count>0){

                errorLabel.setVisible(true);


            } else {

                // WILL INVOKE THE OVERLOADED CONSTRUCTOR AND INSERT THE STRING INTO THE DATABASE
                Playlist newPlaylist = new Playlist(userInput);

                windowOpen=false;

                System.out.println("User created new playlist with the name: " + userInput);

                // WILL RE-DISPLAY / REFRESH THE LIST OF PLAYLISTS
                Controller.doneCreatingPlaylist();


                // TEMPORARY SOLUTION FOR CLOSING THE WINDOW. TAKES THE BUTTON, GETS THE WINDOW FROM IT AND CLOSES IT.
                ((Stage)createPlayListButton.getScene().getWindow()).close();

            }

        }

    }

    /**
     * Will close the window, without saving the name
     */

    @FXML
    private void cancelCreation(){

        // TEMPORARY SOLUTION FOR CLOSING THE WINDOW
        ((Stage)createPlayListButton.getScene().getWindow()).close();

        windowOpen=false;



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
