package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import playlistManagement.Playlist;

import java.io.IOException;

public class PopupController {

    @FXML
    private TextField playListNameTextField;

    @FXML
    private Label errorLabel;

    private boolean windowOpen = false;

    private Stage inputWindow;

    Parent sourceOfWindow;

    public PopupController() throws IOException{


    }


    public void createPlaylistPopup() throws IOException {


        inputWindow = new Stage();
        sourceOfWindow = FXMLLoader.load(getClass().getResource("createPlaylistWindow.fxml"));
        inputWindow.getIcons().add(new Image("sample/images/Music-icon.png"));
        inputWindow.setScene(new Scene(sourceOfWindow, 300, 200));
        inputWindow.setAlwaysOnTop(true);
        inputWindow.setResizable(false);

        if (!windowOpen){

            inputWindow.show();

            windowOpen = true;

        }

        // WILL CHECK IF THE USER CLOSES THE WINDOW AND MAKE YOU ABLE TO REOPEN IT AFTERWARDS
        inputWindow.setOnHiding(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("Playlist Creation Window closed by clicking on Close Button(X)");
                        windowOpen=false;
                        inputWindow.close();
                    }
                });
            }
        });



    }


    @FXML
    private void submitNewPlaylist(){

        String userInput = playListNameTextField.getText();

        if (userInput!=""){

            DB.selectSQL("select count(fldName) from tblPlaylist where fldName='"+userInput+"'");

            int count = Integer.parseInt(DB.getData());

            emptyData();

            if (count>0){

                errorLabel.setVisible(true);
                // errorLabel.setText("You already have a playlist with that name");

            } else {

                Playlist newPlaylist = new Playlist(userInput);

                windowOpen=false;

                System.out.println("User created new playlist with the name: " + userInput);

            //    inputWindow.hide();

            }

        }

    }

    @FXML
    private void cancelCreation(){

        inputWindow.close();


        windowOpen=false;



    }



    private static void emptyData(){


        do{
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)){
                break;
            }

        } while(true);


    }




}
