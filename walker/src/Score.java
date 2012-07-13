/**
 * Created with IntelliJ IDEA.
 * User: lonlylocly
 * Date: 7/13/12
 * Time: 7:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Score {

    public static final int LAMBDA_POINTS = 25;

    public int getScore(Moves m){
        return m.getLambdas() * LAMBDA_POINTS - m.getMoves();
    }

    public int getScore(RockMap r, Moves m, FinishState st){
        int mul = 0;
        switch (st){
            case LIFT:
                mul = 3;
                break;
            case ABORT:
                mul = 2;
                break;
            case DIE:
            case CONTINUE:
            case LIMIT:
                mul = 1;
                break;
            default:
                mul = 0;
        }
        return m.getLambdas() * LAMBDA_POINTS * mul - m.getMoves();
    }
}
