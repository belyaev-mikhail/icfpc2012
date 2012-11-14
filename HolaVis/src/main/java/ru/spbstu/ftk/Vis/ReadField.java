package ru.spbstu.ftk.Vis;

import ru.spbstu.ftk.Walker.Walker;
import ru.spbstu.ftk.Walker.Move;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lonlylocly
 * Date: 7/14/12
 * Time: 6:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadField {

    public static boolean keepWorking = true;

    public static String readField(InputStream is) {
        String str = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String s = in.readLine();
            while (s != null) {
                str += s + "\n";
                s = in.readLine();
            }
        } catch (IOException e) {
        }

        return str;
    }

    public static String readField() {
        return readField(System.in);
    }


    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                keepWorking = false;
            }
        });


        if (args.length >= 1 && args[0].equals("debug")) {
            final FieldControl fs = new FieldControl(readField());
            //System.out.println(fs.getState());

            return;
        }

        final FieldControl fs = new FieldControl(readField());
        List<Character> path = walkWithString(fs);

    }

    public static List<Character> walkWithString(FieldControl fs) {
        final List<Character> allMoves = new LinkedList<Character>();

        boolean gameStopped = false;

        while(!fs.isGameStopped() && keepWorking) {
            final Walker walker = new Walker();
            List<Move> moves = walker.buildRoute(fs);

            if (moves.isEmpty()) {
                moves.add(Move.ABORT);
            }

            for(Move move : moves) {
                fs.playerMove(move);
                fs.startChange();
                fs.step();
                fs.commitChange();

                allMoves.add(move.getRep());
                System.out.print(move.getRep());
                System.out.flush();

                if(fs.isGameStopped()) {
                    break;
                }
            }
        }

        return allMoves;
    }
}
