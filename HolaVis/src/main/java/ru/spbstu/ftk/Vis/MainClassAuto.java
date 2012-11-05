package ru.spbstu.ftk.Vis;

import ru.spbstu.ftk.Walker.Move;
import ru.spbstu.ftk.Walker.Walker;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

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

        final List<Character> allMoves = new LinkedList<Character>();

        new ArrowController(gf) {



            @Override
            public void goUp() {
                if(fs.isGameStopped()) return;
                final Timer timer = new Timer(300,null);
                timer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                            if(!sendSignal()) timer.stop();
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

            @Override
            public void shave() {
            }

            private boolean sendSignal() {

                if(fs.isGameStopped()) {
                    System.out.println("Game finished with " + fs.getFinishingState());
                    System.out.println("Your moves:");
                    for(char m: allMoves) {
                        System.out.print(m);
                    }
                    System.out.println();
                    System.out.println("Points: " + fs.getPoints());
                    return false;
                }

                final Walker walker = new Walker();
                List<Move> moves = walker.buildRoute(fs);
                //System.out.println(moves);

                if (moves.isEmpty()) {
                    moves.add(Move.ABORT);
                }

                for(Move move : moves) {
                    fs.playerMove(move);
                    fs.startChange();
                    fs.step();
                    fs.commitChange();

                    allMoves.add(move.getRep());



                    if(fs.isGameStopped()) {
                        System.out.println("Game finished with " + fs.getFinishingState());
                        System.out.println("Your moves:");
                        for(char m: allMoves) {
                            System.out.print(m);
                        }
                        System.out.println();
                        System.out.println("Points: " + fs.getPoints());
                        return false;
                    }
                }

               // System.out.println("Current points: " + fs.getPoints());
                return true;
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
