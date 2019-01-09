package sample;

import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;

public class MovingText extends JPanel {


    double x = 0;
    double y = 0; // may wanna change this

    public MovingText(double x, double y){

        this.y=y;
        this.x=x;

    }


    @Override
    public void paint(Graphics g){

        super.paint(g);
        Graphics2D gd = (Graphics2D)g;
        gd.drawString("It works man, weeeee!",(int)x,(int)y);

        try{

            Thread.sleep(100); // Setting the speed by specifying the time

            x+=15; // Will increase x by 15 each 100 milliseconds

        } catch (Exception e){

            // TODO HANDLE EXCEPTION
        }

        if (x>getWidth()){

            x=0;
        }

        repaint();


    }

    public void createAnimation(Stage stage){

        // TODO MAKE THIS WORK


    }




}
