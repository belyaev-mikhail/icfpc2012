package Walker;

/**
 * Created with IntelliJ IDEA.
 * User: lonlylocly
 * Date: 7/13/12
 * Time: 7:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Moves {
    int moves = 0;
    int lambdas = 0;

    public Moves(int moves, int lambdas) {
        this.moves = moves;
        this.lambdas = lambdas;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public int getLambdas() {
        return lambdas;
    }

    public void setLambdas(int lambdas) {
        this.lambdas = lambdas;
    }
}
