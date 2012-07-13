
public enum CellState {
    ROBOT('R'),
    WALL('#'),
    ROCK('*'),
    LAMBDA('\\'),
    CLOSED_LIFT('L'),
    OPEN_LIFT('O'),
    EARTH('.'),
    EMPTY(' ');

    char rep;
    CellState(char c) {
       rep = c;
    }

    char getRep() {
        return rep;
    }

    static CellState makeCellState(char c) {
        String possibles = "R#*\\LO. ";
        return CellState.values()[possibles.indexOf(c)];
    }
}
