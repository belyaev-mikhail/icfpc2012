package ru.spbstu.ftk.Vis;

import ru.spbstu.ftk.Walker.Move;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FieldPlayback {
    FieldControl fieldControl;
    Queue<Move> playerPath;
    boolean situationChanging = false;
    int steps = 0;

    public FieldPlayback(List<Move> playerPath, FieldControl fieldControl) {
        this.playerPath = new LinkedList<Move>(playerPath);
        this.fieldControl = fieldControl.clone();
    }

    public void step() {
        fieldControl.playerMove(playerPath.poll());
        fieldControl.startChange();
        fieldControl.step();

        List<FieldControl.Cell> changes = fieldControl.getChanges();
        for(FieldControl.Cell change: changes) {
            if(change.getCs().isRock() || change.getCs() == CellState.LAMBDA){
                situationChanging = true;
                break;
            }

        }

        fieldControl.commitChange();
        steps++;
    }

    public void play() {
        while(!playerPath.isEmpty() &&  !fieldControl.isGameStopped()) {
            step();
        }
    }

    public boolean isSituationChanging() {
        return situationChanging;
    }

    public int getPlayerX() {
        return fieldControl.getState().getPlayerX();
    }

    public int getPlayerY() {
        return fieldControl.getState().getPlayerY();
    }

    public FieldControl getFieldControl() {
        return fieldControl;
    }

    public boolean isOK() {
        return ! fieldControl.playerIsDead();
    }

    public int getSteps() {
        return steps;
    }
}
