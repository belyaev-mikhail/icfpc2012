package Vis;

import Walker.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

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
        final JFrame frame = new JFrame("Da field");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String testField =
                "#########\n" +
                "#.....*.#\n" +
                "#R      L\n" +
                "#\\  ..\\ #\n" +
                "#########\n";

        JTextArea area = new JTextArea(testField);
        area.setFont(new Font("Courier New",0,11));
        JOptionPane.showMessageDialog(null, area);
        testField = area.getText();


        final JGameField gf = new JGameField(new FieldControl(testField));

        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(gf);
        final JTextArea fieldText = new JTextArea(testField);
        fieldText.setFont(new Font("Courier New",0,11));
        pane.add(fieldText);

        final List<Move> allMoves = new LinkedList<Move>();

        final ArrowController arrows = new ArrowController(pane) {

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

                FieldControl fs = gf.getFieldControl();

                if(fs.isGameStopped()) {
                    System.out.println("Game finished with " + fs.getFinishingState());
                    System.out.println("Points: " + fs.getPoints());
                    System.out.println("Moves: ");
                    for(Move mv: allMoves) {
                        System.out.print(mv.getRep());
                    }
                    System.out.println();
                    return;
                }

                System.out.println(fs);
                fs.playerMove(move);
                fs.startChange();
                fs.step();
                fs.commitChange();

                allMoves.add(move);
                if(fs.isGameStopped()) {
                    System.out.println("Game finished with " + fs.getFinishingState());
                    System.out.println("Points: " + fs.getPoints());
                    System.out.println("Moves: ");
                    for(Move mv: allMoves) {
                        System.out.print(mv.getRep());
                    }
                    System.out.println();
                    return;
                } else {
                    System.out.println("Current points: " + fs.getPoints());
                }
            }
        };
        frame.getContentPane().add(pane);




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
