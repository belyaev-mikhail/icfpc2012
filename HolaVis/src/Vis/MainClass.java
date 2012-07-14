package Vis;

import Walker.Move;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

public class MainClass {


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Da field");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String testField =
                "#########\n" +
                "#.....*.#\n" +
                "#R      L\n" +
                "#\\  ..\\ #\n" +
                "#########\n";

        final FieldControl fs = new FieldControl(testField);
        final JGameField gf = new JGameField(fs);
        frame.getContentPane().add(gf);

        new ArrowController(gf) {

            @Override
            public void goUp() {
                sendSignal(Move.UP);
            }

            @Override
            public void goDown() {
                sendSignal(Move.DOWN);
            }

            @Override
            public void goLeft() {
                sendSignal(Move.LEFT);
            }

            @Override
            public void goRight() {
                sendSignal(Move.RIGHT);
            }

            @Override
            public void abortGame() {
                sendSignal(Move.ABORT);
            }

            @Override
            public void standAndWait() {
                sendSignal(Move.WAIT);
            }

            private void sendSignal(Move move) {

                fs.playerMove(move);
                fs.startChange();
                fs.step();
                fs.commitChange();

                System.out.println("Current points: " + fs.getPoints());
            }
        };

        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
