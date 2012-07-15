package Vis;

public enum CellState {
    ROBOT('R'),
    WALL('#'),
    ROCK('*'),
    LAMBDA('\\'),
    CLOSED_LIFT('L'),
    OPEN_LIFT('O'),
    EARTH('.'),
    EMPTY(' '),
    TRAMPOLINE_A('A'),
    TRAMPOLINE_B('B'),
    TRAMPOLINE_C('C'),
    TRAMPOLINE_D('D'),
    TRAMPOLINE_E('E'),
    TRAMPOLINE_F('F'),
    TRAMPOLINE_G('G'),
    TRAMPOLINE_H('H'),
    TRAMPOLINE_I('I'),
    TARGET_1('1'),
    TARGET_2('2'),
    TARGET_3('3'),
    TARGET_4('4'),
    TARGET_5('5'),
    TARGET_6('6'),
    TARGET_7('7'),
    TARGET_8('8'),
    TARGET_9('9'),
    LAMBDAROCK('@');

    char rep;
    CellState(char c) {
       rep = c;
    }

    public boolean isTrampoline(){
        return "ABCDEFGHI".indexOf(rep) != -1;
    }

    public boolean isTarget(){
        return "123456789".indexOf(rep) != -1;
    }

    public boolean isRock(){
        return this == ROCK || this == LAMBDAROCK;
    }

    public char getRep() {
        return rep;
    }

    static CellState makeCellState(char c) {
        String possibles = "R#*\\LO. ABCDEFGHI123456789@";
        if(possibles.indexOf(c) == -1) return CellState.WALL;
        return CellState.values()[possibles.indexOf(c)];
    }
}
