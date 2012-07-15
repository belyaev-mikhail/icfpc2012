package Vis;

import Walker.Move;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FieldPlayback {
    FieldControl fieldControl;
    Queue<Move> playerPath;

    public FieldPlayback(List<Move> playerPath, FieldControl fieldControl) {
        this.playerPath = new LinkedList<Move>(playerPath);
        this.fieldControl = fieldControl.clone();
    }

    public void step() {
        fieldControl.playerMove(playerPath.poll());
        fieldControl.startChange();
        fieldControl.step();
        fieldControl.commitChange();
    }

    public void play() {
        while(!playerPath.isEmpty() &&  !fieldControl.isGameStopped()) {
            step();
        }
    }

    public FieldControl getFieldControl() {
        return fieldControl;
    }
}
