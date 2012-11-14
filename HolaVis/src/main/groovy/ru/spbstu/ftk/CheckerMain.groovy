package ru.spbstu.ftk

import ru.spbstu.ftk.Walker.Walker
import ru.spbstu.ftk.Walker.Move
import ru.spbstu.ftk.Vis.FieldControl
import ru.spbstu.ftk.Vis.JGameField

/**
 * Created with IntelliJ IDEA.
 * User: belyaev
 * Date: 11/5/12
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
class CheckerMain {
    public static void main(String[] args) {

      def path = (System.in.text + "A").getChars().toList()

      String map = new File(args[0]).text

      final FieldControl fs = new FieldControl(map)

      List<Move> moves = path.collect{ char x -> Move.getMove(x) }.findAll{ it != null };
      //System.out.println(moves);

      for(Move move : moves) {
        fs.playerMove(move);
        fs.startChange();
        fs.step();
        fs.commitChange();
      }

      println fs.points

    }
}
