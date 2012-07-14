package Vis;

import Walker.Move;
import Walker.Walker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainClassAuto {


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
                "#.#...*.#\n" +
                "L  #.. R#\n" +
                "#\\ #... #\n" +
                "#########\n";

        JTextArea area = new JTextArea(testField);
        area.setFont(new Font("Courier New",0,11));
        area.setColumns(40);
        area.setRows(30);
        JOptionPane.showMessageDialog(null, area);
        testField = area.getText();

        final FieldControl fs = new FieldControl(testField);
        final JGameField gf = new JGameField(fs);
        frame.getContentPane().add(gf);

        new ArrowController(gf) {



            @Override
            public void goUp() {
                if(fs.isGameStopped()) return;
                Timer timer = new Timer(300, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                sendSignal();
                                //goUp();
                            }
                        });
                }
                });
                timer.start();

            }

            @Override
            public void goDown() {
            }

            @Override
            public void goLeft() {
            }

            @Override
            public void goRight() {
            }

            @Override
            public void abortGame() {
            }

            @Override
            public void standAndWait() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        sendSignal();
                    }
                });

            }

            private void sendSignal() {

                if(fs.isGameStopped()) return;

                final Walker walker = new Walker();
                Move move = walker.buildRoute(fs.getState());
                System.out.println(move);

                {
                    fs.playerMove(move);
                    fs.startChange();
                    fs.step();
                    fs.commitChange();
                }

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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
