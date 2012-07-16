package Walker;

/**
 * Created with IntelliJ IDEA.
 * User: lonlylocly
 * Date: 7/13/12
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Move {
    LEFT('L'),
    RIGHT('R'),
    UP('U'),
    DOWN('D'),
    WAIT('W'),
    ABORT('A'),
    SHAVE('S'),;

    char rep;

    private Move(char rep) {
        this.rep = rep;
    }

    public char getRep() {
        return rep;
    }


}
