package Vis;

import Walker.Walker;
import Walker.Move;

import java.io.BufferedReader;
import java.io.IOException;
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

    public static String readField() {
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

    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equals("debug")) {
            final FieldControl fs = new FieldControl(readField());
            System.out.println(fs.getState());

            return;
        }

        final FieldControl fs = new FieldControl(readField());
        List<Character> path = walkWithString(fs);
        for (Character character : path) {
            System.out.print(character);
        }
        System.out.flush();
    }

    public static List<Character> walkWithString(FieldControl fs) {
        final List<Character> allMoves = new LinkedList<Character>();

        boolean gameStopped = false;

        while(!fs.isGameStopped()) {
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

                if(fs.isGameStopped()) {
                    break;
                }
            }
        }
        return allMoves;
    }
}
