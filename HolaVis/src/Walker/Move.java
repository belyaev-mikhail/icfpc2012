package Walker;

/**
 * Created with IntelliJ IDEA.
 * User: lonlylocly
 * Date: 7/13/12
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Move {
    LEFT(0),
    RIGHT(1),
    UP(2),
    DOWN(3),
    WAIT(4),
    ABORT(5);

    int rep;

    private Move(int rep) {
        this.rep = rep;
    }

    public int getRep() {
        return rep;
    }


}
