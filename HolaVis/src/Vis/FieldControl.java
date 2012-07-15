package Vis;

import Walker.FinishState;
import Walker.Move;
import Walker.Walker.Point;

import javax.swing.event.ChangeEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FieldControl {
    class Change {
        int x;
        int y;
        CellState cs;

        Change(int x, int y, CellState cs) {
            this.x = x;
            this.y = y;
            this.cs = cs;
        }
    }

    private FieldState oldState;
    //private FieldState newState;
    LinkedList<Change> changes = new LinkedList<Change>();

    private int collectedLambdas = 0;
    private int totalTurns = 0;
    private int turnsInWater = 0;
    private int points = 0;

    private boolean gameStopped = false;
    private FinishState finishingState = null;

    public FinishState getFinishingState() {
        return finishingState;
    }

    private FieldControl() {}
    public FieldControl(String repr) {
        oldState = new FieldState(repr);
        //newState = oldState;
    }

    private void applyChanges() {
        Change action = changes.poll();
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

    CellState peekCell(int x, int y) {
        return oldState.peekCell(x,y);
    }

    void setCell(final int x, final int y, final CellState cs) {
        changes.add(new Change(x,y,cs));

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
                CellState.ROCK.equals(toMove) && CellState.EMPTY.equals(peekCell(getPlayerX() + 2, getPlayerY()))) {
            setCell(getPlayerX(), getPlayerY(),CellState.EMPTY);
            setCell(nx,ny,CellState.ROBOT);
            setCell(getPlayerX() +2, getPlayerY(),CellState.ROCK);
            setPlayerX(nx);
            setPlayerY(ny);
            return;
        }
//        If x0 = x 􀀀 1 and y0 = y (i.e. the Robot moves left), (x0; y0) is a Rock, and (x 􀀀 2; y) is Empty.
//        – Additionally, the Rock moves to (x 􀀀 2; y).
        if( nx == getPlayerX() -1 && ny == getPlayerY() &&
                CellState.ROCK.equals(toMove) && CellState.EMPTY.equals(peekCell(getPlayerX() - 2, getPlayerY()))) {
            setCell(getPlayerX(), getPlayerY(),CellState.EMPTY);
            setCell(nx,ny,CellState.ROBOT);
            setCell(getPlayerX() -2, getPlayerY(),CellState.ROCK);
            setPlayerX(nx);
            setPlayerY(ny);
            return;
        }



    }

    public void step() {
        if(totalTurns != 1 && totalTurns % oldState.getFlooding() == 1) {
            System.out.println("Turns: " + totalTurns + " Flooding: " + oldState.getWater());
            System.out.println("FLOOD!");
            oldState.setWater(oldState.getWater() + 1);
        }

        if(getPlayerY() <= oldState.getWater()) {
            turnsInWater++;
        } else {
            turnsInWater = 0;
        }

        for (int y = 0; y < getHeight() ; y++) {
            for (int x = 0; x < getWidth(); x++) {
                // If (x; y) contains a Rock, and (x; y - 1) is Empty:
//                – (x; y) is updated to Empty, (x; y - 1) is updated to Rock.
                if(peekCell(x,y) == CellState.ROCK && peekCell(x,y-1) == CellState.EMPTY) {
                    setCell(x,y,CellState.EMPTY);
                    setCell(x,y-1,CellState.ROCK);
                }
//                 If (x; y) contains a Rock, (x; y -1) contains a Rock, (x+1; y) is Empty and (x+1; y 􀀀1) is Empty:
//                – (x; y) is updated to Empty, (x + 1; y - 1) is updated to Rock.
                if(peekCell(x,y) == CellState.ROCK && peekCell(x,y-1)== CellState.ROCK
                        && peekCell(x+1,y).equals(CellState.EMPTY) && peekCell(x+1, y-1).equals(CellState.EMPTY)) {
                    setCell(x,y,CellState.EMPTY);
                    setCell(x+1,y-1,CellState.ROCK);
                }
//                 If (x; y) contains a Rock, (x; y - 1) contains a Rock, either (x + 1; y) is not Empty or (x + 1; y - 1)
//                is not Empty, (x - 1; y) is Empty and (x - 1; y - 1) is Empty:
//                – (x; y) is updated to Empty, (x - 1; y - 1) is updated to Rock.
                if(peekCell(x,y) == (CellState.ROCK) && peekCell(x,y-1) == (CellState.ROCK) &&
                        (peekCell(x+1,y) != (CellState.EMPTY) || peekCell(x+1,y-1) != (CellState.EMPTY)) &&
                        peekCell(x-1,y) == (CellState.EMPTY) && peekCell(x-1,y-1) == (CellState.EMPTY)){
                    setCell(x,y,CellState.EMPTY);
                    setCell(x-1,y-1,CellState.ROCK);
                }
//                 If (x; y) contains a Rock, (x; y - 1) contains a Lambda, (x + 1; y) is Empty and (x + 1; y - 1) is
//                Empty:
//                – (x; y) is updated to Empty, (x + 1; y - 1) is updated to Rock.
                if(peekCell(x,y) == (CellState.ROCK) && peekCell(x,y-1) == (CellState.LAMBDA)
                        && peekCell(x+1,y) == (CellState.EMPTY) && peekCell(x+1, y-1) == (CellState.EMPTY)) {
                    setCell(x,y,CellState.EMPTY);
                    setCell(x+1,y-1,CellState.ROCK);
                }

//                 If (x; y) contains a Closed Lambda Lift, and there are no Lambdas remaining:
//                – (x; y) is updated to Open Lambda Lift.
                if(peekCell(x,y) == (CellState.CLOSED_LIFT) && getLambdaCounter() == 0 ){
                    setCell(x,y,CellState.OPEN_LIFT);
                }
//                 In all other cases, (x; y) remains unchanged.
            }
        }

        if(turnsInWater > oldState.getWaterproof()) {
            onDeath();
            return;
        }
        for(Change c: changes) {
            if(CellState.ROBOT == (oldState.peekCell(c.x, c.y-1)) &&
                    c.cs == CellState.ROCK) {
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
        stopGame();
        finishingState = FinishState.DIE;
    }

    void onAbort() {
        stopGame();
        points += 1; // compensate for non-immediate abort
        points += collectedLambdas*25;
        finishingState = FinishState.ABORT;
    }

    void onFinish() {
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
        ret.changes = new LinkedList<Change>(this.changes);

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
