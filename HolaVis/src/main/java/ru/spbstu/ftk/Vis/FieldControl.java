package ru.spbstu.ftk.Vis;

import ru.spbstu.ftk.Walker.Move;
import ru.spbstu.ftk.Walker.FinishState;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FieldControl {
    public static class Cell {
        int x;
        int y;
        CellState cs;

        public Cell(int x, int y, CellState cs) {
            this.x = x;
            this.y = y;
            this.cs = cs;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public CellState getCs() {
            return cs;
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "x=" + x +
                    ", y=" + y +
                    ", cs=" + cs +
                    '}';
        }
    }

    private FieldState oldState;
    //private FieldState newState;
    LinkedList<Cell> changes = new LinkedList<Cell>();


    private int collectedLambdas = 0;
    private int totalTurns = 0;
    private int turnsInWater = 0;
    private int points = 0;

    private boolean gameStopped = false;
    private FinishState finishingState = null;

    public LinkedList<Cell> getChanges() {
        return changes;
    }

    public FinishState getFinishingState() {
        return finishingState;
    }

    private FieldControl() {}
    public FieldControl(String repr) {
        oldState = new FieldState(repr);
        //newState = oldState;
    }

    private void applyChanges() {
        Cell action = changes.poll();
        while(action != null) {
            oldState.setCell(action.x, action.y, action.cs);
            action = changes.poll();
        }
    }
    void startChange(){
        applyChanges();
        //newState = oldState.clone();
    }

    void commitChange(){
        //oldState = newState;
        applyChanges();
        onChange();
    }

    int getHeight(){
        return oldState.getHeight();
    }

    int getWidth(){
        return oldState.getWidth();
    }
    CellState getCell(int x, int y) {
        return oldState.getCell(x, y);
    }

    public CellState peekCell(int x, int y) {
        return oldState.peekCell(x,y);
    }

    void setCell(final int x, final int y, final CellState cs) {
        changes.add(new Cell(x, y, cs));

    }

    public void playerMove(Move move) {
        totalTurns ++;
        points -= 1;
        int nx = 0, ny = 0;
        switch (move) {
            case UP:
                nx = getPlayerX();
                ny = getPlayerY() +1;
                break;
            case DOWN:
                nx = getPlayerX();
                ny = getPlayerY() -1;
                break;
            case RIGHT:
                nx = getPlayerX() +1;
                ny = getPlayerY();
                break;
            case LEFT:
                nx = getPlayerX() -1;
                ny = getPlayerY();
                break;
            case WAIT:
                return;
            case ABORT:
                onAbort();
                return;
        }

        CellState toMove = peekCell(nx,ny);

        if( CellState.EARTH.equals(toMove) || CellState.EMPTY.equals(toMove)) {
            setCell(getPlayerX(), getPlayerY(),CellState.EMPTY);
            setCell(nx,ny,CellState.ROBOT);
            setPlayerX(nx);
            setPlayerY(ny);
            return;
        }
        if( CellState.LAMBDA.equals(toMove)) {
            setCell(getPlayerX(), getPlayerY(),CellState.EMPTY);
            setCell(nx,ny,CellState.ROBOT);
            setPlayerX(nx);
            setPlayerY(ny);
            setLambdaCounter(getLambdaCounter() - 1);
            collectedLambdas++;
            points += 25;
            return;
        }

        if( CellState.OPEN_LIFT.equals(toMove)) {
            setCell(getPlayerX(), getPlayerY(),CellState.EMPTY);
            setCell(nx,ny,CellState.ROBOT);
            setPlayerX(nx);
            setPlayerY(ny);
            onFinish();
            return;
        }
//        If x0 = x + 1 and y0 = y (i.e. the Robot moves right), (x0; y0) is a Rock, and (x + 2; y) is Empty.
//        – Additionally, the Rock moves to (x + 2; y).
        if( nx == getPlayerX() +1 && ny == getPlayerY() &&
                toMove.isRock() && CellState.EMPTY.equals(peekCell(getPlayerX() + 2, getPlayerY()))) {
            setCell(getPlayerX(), getPlayerY(),CellState.EMPTY);
            setCell(nx,ny,CellState.ROBOT);
            setCell(getPlayerX() +2, getPlayerY(), toMove);
            setPlayerX(nx);
            setPlayerY(ny);
            return;
        }
//        If x0 = x 􀀀 1 and y0 = y (i.e. the Robot moves left), (x0; y0) is a Rock, and (x 􀀀 2; y) is Empty.
//        – Additionally, the Rock moves to (x 􀀀 2; y).
        if( nx == getPlayerX() -1 && ny == getPlayerY() &&
                toMove.isRock() && CellState.EMPTY.equals(peekCell(getPlayerX() - 2, getPlayerY()))) {
            setCell(getPlayerX(), getPlayerY(),CellState.EMPTY);
            setCell(nx,ny,CellState.ROBOT);
            setCell(getPlayerX() -2, getPlayerY(), toMove);
            setPlayerX(nx);
            setPlayerY(ny);
            return;
        }

        if(toMove == CellState.RAZOR) {
            setCell(getPlayerX(), getPlayerY(),CellState.EMPTY);
            setCell(nx,ny,CellState.ROBOT);
            setPlayerX(nx);
            setPlayerY(ny);
            oldState.setRazors(oldState.getRazors() + 1);
            return;
        }

        if(toMove.isTrampoline()) {
            Map<Character,Character> tramps = oldState.getTrampolines();
            //System.out.println(tramps);
            CellState target = CellState.makeCellState(tramps.get(toMove.getRep()));

//            System.out.println(toMove.getRep());
//            System.out.println(tramps.get(toMove.getRep()));
//            System.out.println(target);
            int tx = -1,ty = -1;
            outer:
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    if(peekCell(x,y) == target) {
                        tx = x;
                        ty = y;
                        break outer;
                    }
                }
            }
            //System.out.println("tx = " + tx + ", ty = " + ty);

            setCell(getPlayerX(), getPlayerY(),CellState.EMPTY);
            setCell(tx, ty, CellState.ROBOT);
            setCell(nx, ny, CellState.EMPTY);
            for(Map.Entry<Character, Character> tramp : tramps.entrySet()) {
                if(tramp.getValue().equals(target.getRep())) {
                    CellState otherTramp = CellState.makeCellState(tramp.getKey());
                    Cell trampCell = findByState(otherTramp);
                    setCell(trampCell.x, trampCell.y, CellState.EMPTY);
                }
            }

            setPlayerX(tx);
            setPlayerY(ty);
            return;

        }

        if(move == Move.SHAVE) {
            int x = getPlayerX();
            int y = getPlayerY();
            int[] dx = {-1,-1,-1, 0, 0, 1, 1, 1};
            int[] dy = {-1, 0, 1, 1,-1, 1, 0,-1};
            for (int i = 0; i < dy.length; i++) {
                CellState gr = peekCell(x + dx[i], y + dy[i]);
                if(gr == CellState.BEARD) setCell(x + dx[i], y + dy[i], CellState.EMPTY);
            }
        }



    }

    Cell findByState(CellState cs) {
        return oldState.findByState(cs);
    }



    public void step() {
        if(oldState.getFlooding() != 0 && totalTurns != 1 && totalTurns % oldState.getFlooding() == 1) {
//            System.out.println("Turns: " + totalTurns + " Flooding: " + oldState.getWater());
//            System.out.println("FLOOD!");
            oldState.setWater(oldState.getWater() + 1);
        }

        if(getPlayerY() <= oldState.getWater()) {
            turnsInWater++;
        } else {
            turnsInWater = 0;
        }

        for (int y = 0; y < getHeight() ; y++) {
            for (int x = 0; x < getWidth(); x++) {
                CellState toMove = peekCell(x,y);
                // If (x; y) contains a Rock, and (x; y - 1) is Empty:
//                – (x; y) is updated to Empty, (x; y - 1) is updated to Rock.
                if(toMove.isRock() && peekCell(x,y-1) == CellState.EMPTY) {
                    setCell(x,y,CellState.EMPTY);
                    if(toMove != CellState.LAMBDAROCK || peekCell(x,y-2) == CellState.EMPTY){
                        setCell(x,y-1,toMove);
                    } else if(toMove == CellState.LAMBDAROCK){
                        setCell(x,y-1,CellState.LAMBDA);
                    }
                }
//                 If (x; y) contains a Rock, (x; y -1) contains a Rock, (x+1; y) is Empty and (x+1; y 􀀀1) is Empty:
//                – (x; y) is updated to Empty, (x + 1; y - 1) is updated to Rock.
                if(toMove.isRock() && peekCell(x,y-1).isRock()
                        && peekCell(x+1,y).equals(CellState.EMPTY) && peekCell(x+1, y-1).equals(CellState.EMPTY)) {
                    setCell(x,y,CellState.EMPTY);
                    if(toMove == CellState.ROCK || peekCell(x+1,y-2) == CellState.EMPTY){
                        setCell(x+1,y-1,toMove);
                    } else if(toMove == CellState.LAMBDAROCK) {
                        setCell(x+1,y-1,CellState.LAMBDA);
                    }

                }
//                 If (x; y) contains a Rock, (x; y - 1) contains a Rock, either (x + 1; y) is not Empty or (x + 1; y - 1)
//                is not Empty, (x - 1; y) is Empty and (x - 1; y - 1) is Empty:
//                – (x; y) is updated to Empty, (x - 1; y - 1) is updated to Rock.
                if(toMove.isRock() && peekCell(x,y-1).isRock() &&
                        (peekCell(x+1,y) != (CellState.EMPTY) || peekCell(x+1,y-1) != (CellState.EMPTY)) &&
                        peekCell(x-1,y) == (CellState.EMPTY) && peekCell(x-1,y-1) == (CellState.EMPTY)){
                    setCell(x,y,CellState.EMPTY);
                    if(toMove == CellState.ROCK || peekCell(x-1,y-2) == CellState.EMPTY){
                        setCell(x-1,y-1,toMove);
                    } else if(toMove == CellState.LAMBDAROCK){
                        setCell(x-1,y-1,CellState.LAMBDA);
                    }
                }
//                 If (x; y) contains a Rock, (x; y - 1) contains a Lambda, (x + 1; y) is Empty and (x + 1; y - 1) is
//                Empty:
//                – (x; y) is updated to Empty, (x + 1; y - 1) is updated to Rock.
                if(toMove.isRock() && peekCell(x,y-1) == (CellState.LAMBDA)
                        && peekCell(x+1,y) == (CellState.EMPTY) && peekCell(x+1, y-1) == (CellState.EMPTY)) {
                    setCell(x,y,CellState.EMPTY);
                    if(toMove == CellState.ROCK || peekCell(x+1,y-2) == CellState.EMPTY){
                        setCell(x+1,y-1,toMove);
                    } else if(toMove == CellState.LAMBDAROCK) {
                        setCell(x+1,y-1,CellState.LAMBDA);
                    }

                }

//                 If (x; y) contains a Closed Lambda Lift, and there are no Lambdas remaining:
//                – (x; y) is updated to Open Lambda Lift.
                if(toMove == (CellState.CLOSED_LIFT) && getLambdaCounter() == 0 ){
                    setCell(x,y,CellState.OPEN_LIFT);
                }

                if(toMove == CellState.BEARD &&
                        oldState.getGrowth() != 0 && totalTurns != 1 && totalTurns % oldState.getGrowth() == 1) {
                    int[] dx = {-1,-1,-1, 0, 0, 1, 1, 1};
                    int[] dy = {-1, 0, 1, 1,-1, 1, 0,-1};
                    for (int i = 0; i < dy.length; i++) {
                        CellState gr = peekCell(x + dx[i], y + dy[i]);
                        if(gr == CellState.EMPTY) setCell(x + dx[i], y + dy[i], CellState.BEARD);
                    }
                }
//                 In all other cases, (x; y) remains unchanged.
            }
        }

        if(turnsInWater > oldState.getWaterproof()) {
            onDeath();
            return;
        }
        for(Cell c: changes) {
            if(CellState.ROBOT == (oldState.peekCell(c.x, c.y-1)) &&
                    (c.cs == CellState.ROCK || c.cs == CellState.LAMBDA)) {
                onDeath();
                return;
            }
        }
    }

    private List<FieldControlListener> listeners = new LinkedList<FieldControlListener>();

    void addListener(FieldControlListener lst) {
        listeners.add(lst);
    }

    void removeListener(FieldControlListener lst) {
        listeners.remove(lst);
    }

    void onChange(){
        for(FieldControlListener lst: listeners) {
            lst.onChange();
        }
    }

    void stopGame() {
        gameStopped = true;
    }

    public boolean isGameStopped() {
        return gameStopped;
    }

    public boolean isCellInWater(int x, int y) {
        return y <= oldState.getWater();
    }

    void onDeath() {
        if(isGameStopped()) return;
        stopGame();
        finishingState = FinishState.DIE;
    }

    void onAbort() {
        if(isGameStopped()) return;

        stopGame();
        points += 1; // compensate for non-immediate abort
        points += collectedLambdas*25;
        finishingState = FinishState.ABORT;
    }

    void onFinish() {
        if(isGameStopped()) return;
        stopGame();
        points += collectedLambdas*50;
        finishingState = FinishState.LIFT;
    }

    public int getPoints() {
        return points;
    }

    public FieldState getState() {
        return oldState;
    }

    private int getPlayerX() {
        return oldState.getPlayerX();
    }

    private void setPlayerX(int playerX) {
        oldState.setPlayerX(playerX);
    }

    private int getPlayerY() {
        return oldState.getPlayerY();
    }

    private void setPlayerY(int playerY) {
        oldState.setPlayerY(playerY);
    }

    private int getLambdaCounter() {
        return oldState.getLambdaCounter();
    }

    private void setLambdaCounter(int lambdaCounter) {
        oldState.setLambdaCounter(lambdaCounter);
    }

    public FieldControl clone() {
        FieldControl ret = new FieldControl();
        ret.oldState = this.oldState.clone();
        //ret.newState = this.newState.clone();
        ret.changes = new LinkedList<Cell>(this.changes);

        ret.points = this.points;
        ret.collectedLambdas = this.collectedLambdas;
        ret.gameStopped = this.gameStopped;
        ret.finishingState = this.finishingState;

        ret.totalTurns = this.totalTurns;
        ret.turnsInWater = this.turnsInWater;


        return ret;
    }

    public boolean playerIsDead() {
        return finishingState == FinishState.DIE;
    }
}
